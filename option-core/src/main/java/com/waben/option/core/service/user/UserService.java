package com.waben.option.core.service.user;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.UserBerealMessage;
import com.waben.option.common.amqp.message.UserLoginMessage;
import com.waben.option.common.amqp.message.UserRegisterMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.resource.ImageCodeAPI;
import com.waben.option.common.interfaces.thirdparty.SmsAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.dto.resource.ConfigIncomeDTO;
import com.waben.option.common.model.dto.resource.LevelIncomeDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.dto.user.*;
import com.waben.option.common.model.enums.*;
import com.waben.option.common.model.query.UserPageQuery;
import com.waben.option.common.model.request.user.*;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.NumberUtil;
import com.waben.option.common.util.PatternUtil;
import com.waben.option.common.util.TimeUtil;
import com.waben.option.common.util.TreeNodeUtil;
import com.waben.option.core.config.RefreshConfig;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.activity.ActivityService;
import com.waben.option.core.service.activity.ActivityUserForbidService;
import com.waben.option.core.service.payment.PaymentOrderService;
import com.waben.option.core.service.payment.WithdrawOrderService;
import com.waben.option.core.service.resource.ConfigService;
import com.waben.option.core.service.resource.MissionActivityService;
import com.waben.option.core.service.statement.AccountStatementService;
import com.waben.option.data.entity.payment.PaymentOrder;
import com.waben.option.data.entity.user.AccountStatement;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.activity.ActivityUserJoinDao;
import com.waben.option.data.repository.order.OrderDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserStaDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.waben.option.common.constants.Constants.LIMIT_USER_LEVEL;

@Slf4j
@Service
public class UserService {

    private static String regEx = "[-+ ]";

    @Resource
    private IdWorker idWorker;
    @Resource
    private UserDao userDao;
    @Resource
    private UserStaDao userStaDao;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private ConfigService configService;
    @Resource
    private AccountService accountService;
    @Resource
    private AccountStatementService accountStatementService;
    @Resource
    private ImageCodeAPI imageCodeAPI;
    @Resource
    private OrderDao orderDao;
    @Resource
    private SmsAPI smsAPI;
    @Resource
    private StaticConfig staticConfig;
    @Resource
    private AMQPService amqpService;
    @Resource
    private ModelMapper modelMapper;
    @Value(value = "${token.expiredSeconds}")
    private long expiredSeconds;
    @Resource
    private Environment env;
    @Resource
    private UserMissionCompleteService userMissionCompleteService;
    @Resource
    private ActivityService activityService;
    @Resource
    private MissionActivityService missionActivityService;
    @Resource
    private ActivityUserForbidService activityForbidService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<Serializable, Object> redisTemplate;
    @Resource
    private RefreshConfig refreshConfig;
    @Resource
    private ActivityUserJoinDao activityUserJoinDao;
    @Resource
    private PaymentOrderService paymentOrderService;
    @Resource
    private WithdrawOrderService withdrawOrderService;
    //    @Resource
//    private LoadingCache<UserQueryDTO, Map<Long, UserTreeDTO>> loadingCache;
//    @Resource
//    private LoadingCache<UserQueryDTO, UserStaDTO> loadingCacheReulst;
    private Random random = new Random();

    /**
     * 登录
     *
     * @param username
     * @param password
     * @param ip
     * @param code
     * @return
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public UserDTO login(String username, String password, String ip, String code) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.USERNAME, username));
        verifyLoginParameter(password, code, user);
        LocalDateTime now = LocalDateTime.now();
        // 判断是否满足真实用户条件
        if (user.getLastLoginTime() != null && now.isBefore(user.getGmtCreate().plusDays(1))) {
            UserSta userSta = userStaDao.selectById(user.getId());
            if (!userSta.getHasFirstLogin()) {
                userSta.setHasFirstLogin(true);
                userStaDao.updateById(userSta);
                if (userSta.getHasFirstShare() && !userSta.getIsReal()) {
                    beRealProducer(user.getId());
                }
            }
        }
        user.setLastLoginTime(now);
        user.setLastLoginIp(ip);
        if (user.getAuthorityType() == AuthorityEnum.CLIENT) {
            loginConsumer(user);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    private void beRealProducer(Long userId) {
        amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_BEREAL,
                new AMQPMessage<UserBerealMessage>(new UserBerealMessage(userId)));
    }

    private void loginConsumer(User user) {
        amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_LOGIN,
                new AMQPMessage<UserLoginMessage>(
                        new UserLoginMessage(user.getId(), user.getLastLoginTime(), user.getLastLoginIp())));
    }

    private void verifyLoginParameter(String password, String code, User user) {
        if (user == null) {
            throw new ServerException(1016);
        }
        if (!passwordEncoder.matches(password, user.getLoginPassword())) {
            throw new ServerException(1017);
        }
        if (StringUtils.isNotEmpty(user.getAreaCode())) {
            if (user.getAuthorityType() == AuthorityEnum.CLIENT) {
                if (code != null && !code.equals(user.getAreaCode()))
                    throw new ServerException(1054);
            }
        }

    }

    /**
     * 注册
     *
     * @param request
     * @return
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterUserRequest request) {
        String checkIp = request.getIp();
        if (!StringUtils.isBlank(request.getIp())) {
            checkIp = checkIp.trim();
            checkIp = checkIp.replaceAll("\\.", "_");
            checkIp = checkIp.replaceAll("\\:", "_");
        }
//        if (!StringUtils.isBlank(checkIp) && !StringUtils.isBlank(request.getSymbolCode())) {
//            Object checkValue = redisTemplate.opsForValue()
//                    .get(RedisKey.OPTION_USER_IP_REGISTER_KEY + request.getSymbolCode().trim() + "_" + checkIp);
//            if (checkValue != null) {
//                throw new ServerException(1061);
//            }
//        }
       verifyRegisterParameter(request);
        List<User> userList = userDao.selectList(new QueryWrapper<User>().eq(User.USERNAME, request.getUsername()));
        if (!CollectionUtils.isEmpty(userList)) {
            if (request.getRegisterType() == RegisterEnum.EMAIL) {
                throw new ServerException(1043);
            }
            throw new ServerException(1023);
        }
        Integer groupIndex = getGroupIndex();
        User createUser = createUser(request, groupIndex);
        redisTemplate.opsForValue().set(RedisKey.OPTION_USER_GROUP_KEY, groupIndex);
        if (!StringUtils.isBlank(checkIp) && !StringUtils.isBlank(request.getSymbolCode())
                && refreshConfig.getInviteIpInterval().intValue() > 0) {
            redisTemplate.opsForValue().set(
                    RedisKey.OPTION_USER_IP_REGISTER_KEY + request.getSymbolCode().trim() + "_" + checkIp, true,
                    refreshConfig.getInviteIpInterval().intValue(), TimeUnit.MINUTES);
        }
        registerConsumer(createUser);
        return modelMapper.map(createUser, UserDTO.class);
    }

    private Integer getGroupIndex() {
        Integer groupIndex = (Integer) redisTemplate.opsForValue().get(RedisKey.OPTION_USER_GROUP_KEY);
        if (groupIndex == null) {
            groupIndex = 1;
        } else {
            groupIndex = groupIndex + 1;
            if (groupIndex > 20) {
                groupIndex = 1;
            }
        }
        return groupIndex;
    }

    private void registerConsumer(User user) {
        amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_REGISTER,
                new AMQPMessage<UserRegisterMessage>(new UserRegisterMessage(user.getId(), user.getParentId())));
    }

    private void registerConsumer(Long userId) {
        amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_REGISTER,
                new AMQPMessage<UserRegisterMessage>(new UserRegisterMessage(userId)));
    }

    //    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public User createUser(RegisterUserRequest request, Integer groupIndex) {
        User user = new User();
        user.setId(idWorker.nextId());
        user.setUsername(request.getUsername());
        user.setRegisterType(request.getRegisterType());
        if (request.getRegisterType() == RegisterEnum.PHONE) {
            user.setMobilePhone(request.getUsername());
            user.setAreaCode(request.getAreaCode());
        } else {
            user.setEmail(request.getUsername());
        }
        user.setAuthorityType(request.getAuthorityType());
        user.setRegisterPlatform(request.getPlatform());
        user.setLoginPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegisterIp(request.getIp());
        user.setSource(request.getSource());
        user.setUid(NumberUtil.generateCode(8));
        user.setNickname(request.getUsername());
        user.setSymbol(createSymbol(request.getSymbolCode(), user));
        user.setSymbolCode(NumberUtil.generateCode(8));
        if(StringUtils.isNotBlank(request.getSymbolCode())){
            user.setParentSymbolCode(request.getSymbolCode());
            QueryWrapper<User> query = new QueryWrapper<>();
            query = query.eq(User.SYMBOL_CODE, request.getSymbolCode());
            User userTopId = userDao.selectOne(query);
            if (null != userTopId){
                user.setTopId(StringUtils.isNotBlank(userTopId.getTopId())?userTopId.getTopId():userTopId.getSymbolCode());
            }
        }else {
            user.setParentSymbolCode("0");
        }
        user.setGroupIndex(groupIndex);
        user.setIsVip(false);
        user.setIsBlack(false);
        user.setIsReal(false);
        userDao.insert(user);
        UserSta userSta = new UserSta();
        userSta.setId(user.getId());
        userSta.setUid(user.getUid());
        userSta.setUsername(user.getUsername());
        userSta.setParentId(user.getParentId());
        userSta.setTotalRechargeAmount(BigDecimal.ZERO);
        userSta.setTotalWithdrawAmount(BigDecimal.ZERO);
        userSta.setInviteCount(0);
        userSta.setInviteRechargeCount(0);
        userSta.setIsBlack(false);
        userSta.setIsGenerate(false);
        userSta.setIsReal(false);
        userStaDao.insert(userSta);
        createUserAccount(user);
        return user;
    }

    public int registerIpCount(String registerIp) {
        if (registerIp != null) {
            return userDao.selectCount(new QueryWrapper<User>().eq(User.REGISTER_IP, registerIp));
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    void createUserAccount(User user) {
        List<String> currencyList = new ArrayList<>();
        String currencyConfig = configService.queryConfig(DBConstants.CONFIG_CURRENCY).getValue();
        if (StringUtils.isEmpty(currencyConfig)) {
            currencyList.add(staticConfig.getDefaultCurrency().name());
        } else {
            String[] currencyArr = currencyConfig.trim().split(",");
            for (String currency : currencyArr) {
                currencyList.add(currency.trim());
            }
        }
        for (int i = 0; i < currencyList.size(); i++) {
            String currency = currencyList.get(i);
            accountService.initAccount(user.getId(), BigDecimal.ZERO, CurrencyEnum.valueOf(currency),
                    user.getGroupIndex());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void createInviteRegister(Long userId, Long parentUserId) {
        User user = userDao.selectById(userId);
        if (user.getParentId() != null && user.getParentId() != 0L) {
            UpdateJoinDTO updateJoinDTO = new UpdateJoinDTO();
            updateJoinDTO.setType(ActivityTypeEnum.INVITE);
            updateJoinDTO.setUserId(parentUserId);
            updateJoinDTO.setQuantity(BigDecimal.ONE);
            updateJoinDTO.setJoinUserId(userId);
            activityService.updateJoin(updateJoinDTO);
        }
    }

    private void verifyRegisterParameter(RegisterUserRequest request) {
//        String[] activeProfiles = env.getActiveProfiles();
//        if ("prod".equals(activeProfiles[0])) {
//            int registerIpCount = registerIpCount(request.getIp());
//            if (registerIpCount > 10)
//                throw new ServerException(1061);
//        }
        if (request.getVerifyCode() != null ) {
            smsAPI.verifyCode(request.getUsername(), request.getVerifyCode());
        }
//        if (request.getRegisterType() == RegisterEnum.PHONE) {
//            if (request.getVerifyCode() == null)
//                throw new ServerException(1026);
//            if (!PatternUtil.isMobile(request.getUsername()) || request.getUsername().length() < 5) {
//                throw new ServerException(1021);
//            }
//        } else {
            // 验证邮箱，防止临时邮箱注册
//            if (!refreshConfig.checkRegisterEmailAllow(request.getUsername())) {
//                throw new ServerException(2063);
//            }
//            if (request.getSource() == 1 && request.getVerifyCode() == null) {
//                throw new ServerException(1026);
//            }
//            if (!PatternUtil.isEmail(request.getUsername())) {
//                throw new ServerException(1022);
//            }
//        }
    }

    /**
     * 用户退出方法
     *
     * @param userId
     * @return
     */
    @Async
    public Boolean logout(Long userId) {
        cleanToken(userId);
        return true;
    }

    /**
     * 根据用户ID获取用户
     *
     * @param userId
     * @return
     */
    public UserDTO queryUser(Long userId) {
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new ServerException(1016);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public UserDTO queryByUsername(String username) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.USERNAME, username));
        if (user == null) {
            throw new ServerException(1016);
        }
        return modelMapper.map(user, UserDTO.class);
    }

    public List<UserDTO> queryUserList(List<Long> uidList) {
        List<User> userList = userDao.selectList(new QueryWrapper<User>().in(User.ID, uidList));
        if (!CollectionUtils.isEmpty(uidList)) {
            return userList.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());
        }
        return null;
    }

    public void storeToken(Long uid, String token) {
        String key = RedisKey.OPTION_USER_KEY + uid;
        ValueOperations<Serializable, Object> ops = redisTemplate.opsForValue();
        ops.set(key, token);
        redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
    }

    public void cleanToken(Long userId) {
        redisTemplate.delete(RedisKey.OPTION_USER_KEY + userId);
    }

    public boolean verifyUsername(String username) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.USERNAME, username));
        if (user == null) {
            return false;
        }
        return true;
    }

    /**
     * 根据旧密码修改登录密码
     *
     * @param username
     * @param oldPassword
     * @param newPassword
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginPassword1(Long userId, String oldPassword, String newPassword) {
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new ServerException(1016);
        }
        if (!StringUtils.isBlank(oldPassword)) {
            if (!passwordEncoder.matches(oldPassword, user.getLoginPassword())) {
                throw new ServerException(1024);
            }
        }
        if (StringUtils.isBlank(newPassword)) {
            throw new ServerException(1025);
        }
        user.setLoginPassword(passwordEncoder.encode(newPassword));
        userDao.updateById(user);
        logout(user.getId());
    }

    /**
     * 根据验证码修改登录密码
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void updateLoginPassword2(UpdatePassword2Request request) {
        User user = buildUpdatePasswordUser(request);
        user.setLoginPassword(passwordEncoder.encode(request.getNewPassword()));
        userDao.updateById(user);
    }

    /**
     * 后台重置登录密码
     *
     * @param userId
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void resetLoginPassword(Long userId, String password) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.ID, userId));
        if (user == null) {
            throw new ServerException(1016);
        }
        user.setLoginPassword(passwordEncoder.encode(password));
        userDao.updateById(user);
    }

    /**
     * 根据旧密码修改支付密码
     *
     * @param username
     * @param oldPassword
     * @param newPassword
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void updateFundPassword1(Long userId, String oldPassword, String newPassword) {
        User user = userDao.selectById(userId);
        if (user == null) {
            throw new ServerException(1016);
        }
        if (!StringUtils.isBlank(oldPassword) && !StringUtils.isBlank(user.getLoginPassword())) {
            if (!passwordEncoder.matches(oldPassword, user.getLoginPassword())) {
                throw new ServerException(1024);
            }
        }
        if (StringUtils.isBlank(newPassword)) {
            throw new ServerException(1025);
        }
        user.setFundPassword(passwordEncoder.encode(newPassword));
        userDao.updateById(user);
    }

    /**
     * 根据验证码修改支付密码
     */
//    @ShardingTransactionType(value = TransactionType.XA)
    @Transactional(rollbackFor = Exception.class)
    public void updateFundPassword2(UpdatePassword2Request request) {
        User user = buildUpdatePasswordUser(request);
        user.setFundPassword(passwordEncoder.encode(request.getNewPassword()));
        userDao.updateById(user);
    }

    private User buildUpdatePasswordUser(UpdatePassword2Request request) {
        User user = userDao.selectById(request.getUserId());
        if (user == null) {
            throw new ServerException(1016);
        }
        if (StringUtils.isBlank(request.getVerifyCode())) {
            throw new ServerException(1026);
        }
        if (StringUtils.isBlank(request.getNewPassword())) {
            throw new ServerException(1025);
        }
        if (user.getAreaCode() != null && !request.getAreaCode().equals(user.getAreaCode())) {
            throw new ServerException(1054);
        }
        Boolean verify = smsAPI.verifyCode(user.getUsername(), request.getVerifyCode());
        if (!verify) {
            throw new ServerException(1031);
        }
        return user;
    }

    /**
     * 更新用户基本信息
     *
     * @param request
     */
    public void updateUserBasic(UpdateUserBasicRequest request) {
        User user = userDao.selectById(request.getUserId());
        if (user == null)
            throw new ServerException(1016);
        if (!StringUtils.isBlank(request.getMobilePhone())) {
            user.setMobilePhone(request.getMobilePhone().replaceAll(regEx, "").replaceAll(" ", ""));
        }
        if (!StringUtils.isBlank(request.getAreaCode())) {
            user.setAreaCode(request.getAreaCode().trim());
        }
        if (!StringUtils.isBlank(request.getEmail())) {
            user.setEmail(request.getEmail().trim());
        }
        if (!StringUtils.isBlank(request.getNickname())) {
            user.setNickname(request.getNickname().trim());
        }
        if (!StringUtils.isBlank(request.getSurname())) {
            user.setSurname(request.getSurname().trim());
        }
        if (!StringUtils.isBlank(request.getName())) {
            user.setName(request.getName().trim());
        }
        if (!StringUtils.isBlank(request.getCountry())) {
            user.setCountry(request.getCountry().trim());
        }
        if (!StringUtils.isBlank(request.getCountryCode())) {
            user.setCountryCode(request.getCountryCode().trim());
        }
        if (!StringUtils.isBlank(request.getCity())) {
            user.setCity(request.getCity().trim());
        }
        if (!StringUtils.isBlank(request.getPostalCode())) {
            user.setPostalCode(request.getPostalCode().trim());
        }
        if (!StringUtils.isBlank(request.getAddress())) {
            user.setAddress(request.getAddress().trim());
        }
        if (!StringUtils.isBlank(request.getAddressDetails())) {
            user.setAddressDetails(request.getAddressDetails().trim());
        }
        if (!StringUtils.isBlank(request.getHeadImg())) {
            user.setHeadImg(request.getHeadImg());
        }
        if (!StringUtils.isBlank(request.getCpfCode())) {
            user.setCpfCode(request.getCpfCode().trim());
        }
        if (!StringUtils.isBlank(request.getCnpj())) {
            user.setCnpj(request.getCnpj().trim());
        }
        if (!StringUtils.isBlank(request.getEvp())) {
            user.setEvp(request.getEvp().trim());
        }
        userDao.updateById(user);
    }

    /**
     * 分页查询用户列表
     *
     * @param userQuery
     * @return
     */
    public PageInfo<UserDTO> queryUserPage(UserPageQuery userQuery) {
        QueryWrapper<User> query = new QueryWrapper<>();
        if (!CollectionUtils.isEmpty(userQuery.getIdList())) {
            query = query.in(User.ID, userQuery.getIdList());
        }
        if (StringUtils.isNotEmpty(userQuery.getUsername())) {
            query = query.like(User.USERNAME, userQuery.getUsername()).or().like(User.ID, userQuery.getUsername()).or()
                    .like(User.UID, userQuery.getUsername());
        }
        if (userQuery.getRegisterStart() != null) {
            query = query.ge(User.GMT_CREATE, userQuery.getRegisterStart());
        }
        if (userQuery.getRegisterEnd() != null) {
            query = query.le(User.GMT_CREATE, userQuery.getRegisterEnd());
        }
        if (userQuery.getLastLoginStart() != null) {
            query = query.ge(User.LAST_LOGIN_TIME, userQuery.getLastLoginStart());
        }
        if (userQuery.getLastLoginEnd() != null) {
            query = query.le(User.LAST_LOGIN_TIME, userQuery.getLastLoginEnd());
        }
        if (StringUtils.isNotEmpty(userQuery.getName())) {
            query = query.eq(User.NAME, userQuery.getName());
        }
        if (userQuery.getAuthorityType() != null) {
            query = query.eq(User.AUTHORITY_TYPE, userQuery.getAuthorityType());
        }
        if (userQuery.getRegisterType() != null) {
            query = query.eq(User.REGISTER_TYPE, userQuery.getRegisterType());
        }
        if (userQuery.getSource() != null) {
            query = query.eq(User.SOURCE, userQuery.getSource());
        }
        query = query.orderByDesc(User.GMT_CREATE);
        PageInfo<UserDTO> pageInfo = new PageInfo<>();
        IPage<User> userIPage = userDao.selectPage(new Page<>(userQuery.getPage(), userQuery.getSize()), query);
        if (userIPage.getTotal() > 0) {
            List<UserDTO> userList = userIPage.getRecords().stream().map(user -> modelMapper.map(user, UserDTO.class))
                    .collect(Collectors.toList());
            pageInfo.setRecords(userList);
            pageInfo.setTotal(userIPage.getTotal());
            pageInfo.setPage((int) userIPage.getPages());
            pageInfo.setSize((int) userIPage.getSize());
            return pageInfo;
        }
        return pageInfo;
    }

    /**
     * 根据层级代码获取用户数据
     *
     * @param symbolCode
     * @return
     */
    public User queryUserBySymbolCode(String symbolCode) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.SYMBOL_CODE, symbolCode));
        if (user == null) {
            throw new ServerException(1016);
        }
        return user;
    }

    /**
     * 生成层级编码
     *
     * @param symbolCode
     * @return
     */
    public synchronized String createSymbol(String symbolCode, User user) {
        String maxSymbol;
        Long userId = 0L;
        String parentSymbol = "";
        if (!StringUtils.isBlank(symbolCode)) {
            User parent = queryUserBySymbolCode(symbolCode);
            userId = parent.getId();
            user.setParentId(userId);
            parentSymbol = parent.getSymbol();
            // 检查是否禁止邀请用户
            if (activityForbidService.isForbid(parent.getId(), ActivityTypeEnum.INVITE)) {
                log.info(parent.getId() + " invite " + user.getId() + ", but is forbidden");
                user.setParentId(0L);
                maxSymbol = userDao.queryMaxSymbol(0L);
            } else {
                // 检查是否触发邀请门槛
                boolean hidden = false;
                UserSta parentSta = userStaDao.selectById(parent.getId());
                Integer inviteCount = parentSta.getInviteCount();
                if (refreshConfig.getInviteThreshold().intValue() > 0 && inviteCount != null
                        && inviteCount.intValue() >= refreshConfig.getInviteThreshold().intValue()) {
                    int hiddenRandom = random.nextInt(100);
                    if (hiddenRandom < refreshConfig.getInviteHiddenValue().intValue()) {
                        hidden = true;
                    }
                }
                if (hidden) {
                    log.info(parent.getId() + " invite " + user.getId() + ", but is forbidden");
                    user.setParentId(0L);
                    maxSymbol = userDao.queryMaxSymbol(0L);
                } else {
                    if (inviteCount == null) {
                        inviteCount = 1;
                    }
                    inviteCount += 1;
                    parentSta.setInviteCount(inviteCount);
                    userStaDao.updateById(parentSta);
                    user.setParentId(parent.getId());
                    userId = parent.getId();
                    parentSymbol = parent.getSymbol();
                    maxSymbol = userDao.queryMaxSymbol(parent.getId());
                }
            }
            user.setRealParentId(parent.getId());
        } else {
            user.setParentId(0L);
            maxSymbol = userDao.queryMaxSymbol(0L);
        }
        //20220909 sj add
//        if (StringUtils.isBlank(maxSymbol)){
//            maxSymbol = userDao.queryMaxSymbol(0L);
//        }
        maxSymbol = getCacheSymbol(userId, maxSymbol, parentSymbol);
        return maxSymbol;
    }

    private String getCacheSymbol(Long userId, String maxSymbol, String parentSymbolCode) {
        String key = RedisKey.getKey(RedisKey.OPTION_USER_MAX_SYMBOL_KEY, userId);
        String cacheSymbol = (String) redisTemplate.opsForValue().get(key);
        log.info("注册使用maxSymbol[{}]key[{}]cacheSymbol[{}]",maxSymbol, key, cacheSymbol);
        if (StringUtils.isNotBlank(maxSymbol) && cacheSymbol != null && maxSymbol.compareTo(cacheSymbol) < 0) {
            maxSymbol = cacheSymbol;
        }
        maxSymbol = getNextCode(maxSymbol, parentSymbolCode);
        redisTemplate.opsForValue().set(key, maxSymbol);
        return maxSymbol;
    }

    private String getNextCode(String maxRoleCode, String parentSymbolCode) {
        String resultRoleCode;
        if (maxRoleCode == null) {
            resultRoleCode = parentSymbolCode + "00001";
        } else {
            String subMaxRoleCode = maxRoleCode.substring(maxRoleCode.length() - 5);
            long maxRoleCodeInt = Long.parseLong(subMaxRoleCode) + 1L;
            String afterCode = "00000" + maxRoleCodeInt;
            afterCode = afterCode.substring(afterCode.length() - subMaxRoleCode.length());
            String frontMaxCode = maxRoleCode.substring(0, (maxRoleCode.length() - 5));
            resultRoleCode = frontMaxCode + afterCode;
        }
        return resultRoleCode;
    }

    /**
     * 用户收益
     *
     * @param userId
     * @return
     */
    public UserIncomeDTO queryIncome(Long userId) {
        UserIncomeDTO userIncomeDTO = new UserIncomeDTO();
        User user = userDao.selectById(userId);
        if (user != null && user.getGroupIndex() != null) {
            BigDecimal inviteIncome = queryIncomeTeam(userId, user.getGroupIndex());
            userIncomeDTO.setIncomeTeam(inviteIncome);
            userIncomeDTO.setIncomeGrand(inviteIncome);
            // userIncomeDTO.setIncomeGrand(queryIncomeGrand(userId, user.getGroupIndex()));
        }
        return userIncomeDTO;
    }

    /**
     * 获取团队收益
     *
     * @param userId
     * @return
     */
    private BigDecimal queryIncomeTeam(Long userId, Integer groupIndex) {
        QueryWrapper<AccountStatement> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in(AccountStatement.TYPE, TransactionEnum.CREDIT_INVITE_REGISTER,
//                        TransactionEnum.CREDIT_INVITE_WAGER, TransactionEnum.CREDIT_SUBORDINATE)
//                .eq(AccountStatement.USER_ID, userId);
        queryWrapper.in(AccountStatement.TYPE, TransactionEnum.CREDIT_INVITE_WAGER, TransactionEnum.CREDIT_SUBORDINATE)
                .eq(AccountStatement.USER_ID, userId);
        return getStatementAmount(queryWrapper, groupIndex);
    }

    /**
     * 获取累计收益
     *
     * @param userId
     * @return
     */
    @SuppressWarnings("unused")
    private BigDecimal queryIncomeGrand(Long userId, Integer groupIndex) {
        QueryWrapper<AccountStatement> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .in(AccountStatement.TYPE, TransactionEnum.CREDIT_PROFIT, TransactionEnum.CREDIT_LOGIN_PROFIT,
                        TransactionEnum.CREDIT_ACTIVITY_WAGER, TransactionEnum.CREDIT_LUCKY_DRAW,
                        TransactionEnum.CREDIT_MOVEMENT, TransactionEnum.CREDIT_SUNSHINE)
                .eq(AccountStatement.USER_ID, userId);
        return getStatementAmount(queryWrapper, groupIndex);
    }

    /**
     * 获取我的排名
     *
     * @param userId
     * @param rankType yesterday:昨日排名 thrityDay:30天排名
     * @return
     */
    public int queryRank(Long userId, String rankType) {
        int rank = 1562;

        return rank;
    }

    public ConfigIncomeDTO queryConfigIncome() {
        ConfigIncomeDTO configIncomeDTO = new ConfigIncomeDTO();
        List<LevelIncomeDTO> configList = configService.queryLevelIncome();
        if (!CollectionUtils.isEmpty(configList)) {
            for (LevelIncomeDTO levelIncomeDTO : configList) {
                if (levelIncomeDTO.getLevel() == 1) {
                    configIncomeDTO.setOneIncome(levelIncomeDTO.getIncome());
                }
                if (levelIncomeDTO.getLevel() == 2) {
                    configIncomeDTO.setTwoIncome(levelIncomeDTO.getIncome());
                }
            }
        }
        return configIncomeDTO;
    }

    /**
     * 生成分成流水
     *
     * @param amount
     * @param userId
     */
    public void userRatioDivide(BigDecimal amount, Long userId) {
        ConfigIncomeDTO configIncomeDTO = queryConfigIncome();
        User user = userDao.selectById(userId);
        // 生成yi级流水
        if (user != null && user.getParentId() != 0L) {
            User userOnly = userDao.selectById(user.getParentId());
            if (userOnly != null) {
                BigDecimal twoAmount = configIncomeDTO.getOneIncome().multiply(amount);
                List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                transactionBeanList.add(AccountTransactionBean.builder().userId(userOnly.getId())
                        .type(TransactionEnum.CREDIT_SUBORDINATE).amount(twoAmount).transactionId(user.getParentId())
                        .currency(staticConfig.getDefaultCurrency()).remark(null).build());
                accountService.transaction(userOnly.getId(), transactionBeanList);
            }
            // 生成er级流水
//            if (userOnly != null && userOnly.getParentId() != 0L) {
//                User userTwo = userDao.selectById(userOnly.getParentId());
//                if (userTwo != null) {
//                    BigDecimal oneAmount = configIncomeDTO.getTwoIncome().multiply(amount);
//                    List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
//                    transactionBeanList.add(AccountTransactionBean.builder().userId(userTwo.getId()).type(TransactionEnum.CREDIT_SUBORDINATE)
//                            .amount(oneAmount).transactionId(userOnly.getParentId()).currency(CurrencyEnum.IDR).remark(null).build());
//                    accountService.transaction(userTwo.getId(), transactionBeanList);
//                }
//            }
        }
    }

    public List<UserInviteTreeDTO> queryUserTreeNodeRebuild(Long userId, int level, String inviteAuditStatus) {
        List<UserInviteTreeDTO> list = converToTreeNodeDto(userId, level, inviteAuditStatus);
        if (null != list) {
            for (UserInviteTreeDTO inviteTreeDTO : list) {
                sta(inviteTreeDTO);
            }
        }
        return list;
    }

    public PageInfo<UserInviteTreeDTO> queryUserTreeNodeRebuildNew(Long userId, int level, Long childUserId,int page,int size) {
        PageInfo<UserInviteTreeDTO> list = converToTreeNodeDtoNew(userId, level, childUserId,page,size);
        if (null != list.getRecords()) {
            for (UserInviteTreeDTO inviteTreeDTO : list.getRecords()) {
                sta(inviteTreeDTO);
            }
        }
        return list;
    }

    public List<UserInviteTreeDTO> converToTreeNodeDto(Long userId, int queryLevel, String inviteAuditStatus) {
        return activityUserJoinDao.queryList(userId, queryLevel, inviteAuditStatus);
    }

    public PageInfo<UserInviteTreeDTO> converToTreeNodeDtoNew(Long userId, int queryLevel, Long childUserId,int page, int size) {

        PageInfo<UserInviteTreeDTO> pageInfo = new PageInfo<>();
        if (queryLevel == 1) {
            int count = activityUserJoinDao.queryListDemoLevelCount(userId,childUserId);
            if (count>0) {
                List<UserInviteTreeDTO> treeDTOList = activityUserJoinDao.queryListDemoLevel(userId, page, size,childUserId);
                pageInfo.setRecords(treeDTOList);
                pageInfo.setTotal(count);
                pageInfo.setPage(page);
                pageInfo.setSize(size);
                return pageInfo;
            }
        }

        if (queryLevel == 2) {
            int count = activityUserJoinDao.queryListDemoLeve2Count(userId,childUserId);
            if (count>0){
                List<UserInviteTreeDTO> treeDTOList = activityUserJoinDao.queryListDemoLeve2(userId, page, size,childUserId);
                pageInfo.setRecords(treeDTOList);
                pageInfo.setTotal(count);
                pageInfo.setPage(page);
                pageInfo.setSize(size);
                return pageInfo;
            }
        }

        if (queryLevel == 3) {
            int count = activityUserJoinDao.queryListDemoLeve3Count(userId,childUserId);
            if(count>0){
                List<UserInviteTreeDTO> treeDTOList = activityUserJoinDao.queryListDemoLeve3(userId, page, size,childUserId);
                pageInfo.setRecords(treeDTOList);
                pageInfo.setTotal(count);
                pageInfo.setPage(page);
                pageInfo.setSize(size);
                return pageInfo;
            }
        }
        return pageInfo;
    }

    /**
     * 查询分级用户信息
     */
    public List<UserTreeNodeDTO> converToTreeNodeDto(User user, int level, int queryLevel) {
        List<UserTreeNodeDTO> list = new LinkedList<>();
        if (level > LIMIT_USER_LEVEL) {
            return list;
        }
        if (level > queryLevel) {
            return list;
        }
        List<User> children = userDao.selectList(new QueryWrapper<User>().eq(User.PARENT_ID, user.getId()));
        if (children != null && children.size() > 0) {
            for (User child : children) {
                if (queryLevel == level) {
                    UserTreeNodeDTO userTreeNodeDTO = modelMapper.map(child, UserTreeNodeDTO.class);
                    list.add(userTreeNodeDTO);
                } else {
                    list.addAll(converToTreeNodeDto(child, level + 1, queryLevel));
                }
            }
        }
        return list;
    }

    @Deprecated
    public List<UserTreeNodeDTO> queryUserTreeNode(Long userId) {
        LocalDate localDate = LocalDate.now();
        User us = userDao.selectById(userId);
        User pUser = userDao.selectById(us.getParentId());
        List<UserTreeNodeDTO> treeNodes = new ArrayList<>();
        List<User> userList = userDao.queryUserBySymbolLike(userId, us.getSymbol());
        for (User user : userList) {
            UserTreeNodeDTO treeNodeDTO = modelMapper.map(user, UserTreeNodeDTO.class);
            BigDecimal totalTeamIncome;
            if (userId.equals(user.getId())) {
                totalTeamIncome = orderDao.queryTotalTeamIncome(user.getId(), null);
            } else {
                totalTeamIncome = orderDao.queryTotalTeamIncome(user.getId(), false);
            }
            treeNodeDTO.setTotalContribution(totalTeamIncome == null ? BigDecimal.ZERO : totalTeamIncome);
            treeNodeDTO.setYesterdayContribution(subordinate(user.getId(), localDate, user.getGroupIndex()));
            treeNodes.add(treeNodeDTO);
        }
        return TreeNodeUtil.buildByRecursive(treeNodes, us.getParentId(), pUser == null ? "" : pUser.getUsername());
    }

    private BigDecimal subordinate(Long userId, LocalDate localDate, Integer groupIndex) {
        QueryWrapper<AccountStatement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AccountStatement.TYPE, TransactionEnum.CREDIT_PROFIT).eq(AccountStatement.USER_ID, userId);
        if (localDate != null) {
            queryWrapper.likeRight(AccountStatement.GMT_CREATE, localDate);
        }
        return getStatementAmount(queryWrapper, groupIndex);
    }

    private BigDecimal getStatementAmount(QueryWrapper<AccountStatement> queryWrapper, Integer groupIndex) {
        List<UserAccountStatementDTO> list = accountStatementService.selectList(queryWrapper, groupIndex);
        BigDecimal amount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(list)) {
            for (UserAccountStatementDTO accountStatement : list) {
                amount = amount.add(accountStatement.getAmount());
            }
        }
        return amount;
    }


    public Integer invitePeopleByUsers(String symbol) {
        return userDao.invitePeopleByUsers(symbol);
    }

    public Long queryMobilePhone(String mobilePhone) {
        User user = userDao.selectOne(new QueryWrapper<User>().eq(User.MOBILE_PHONE, mobilePhone));
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    public PageInfo<UserDTO> subordinatePage(UserSubordinateRequest req) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq(User.PARENT_ID, req.getUserId());
        Page<User> page = new Page<>(req.getPage(), req.getSize());
        query.orderByDesc(User.GMT_CREATE);
        IPage<User> pageData = userDao.selectPage(page, query);
        return new PageInfo<>(pageData.getRecords().stream().map(temp -> {
            UserDTO dto = modelMapper.map(temp, UserDTO.class);
            dto.setLoginPassword(null);
            return dto;
        }).collect(Collectors.toList()), pageData.getTotal(), req.getPage(), req.getSize());
    }

    @Transactional
    public void black(Long id) {
        User user = userDao.selectById(id);
        if (user != null) {
            user.setIsBlack(true);
            userDao.updateById(user);
            UserSta userSta = userStaDao.selectById(id);
            userSta.setIsBlack(true);
            userStaDao.updateById(userSta);
        }
    }

    @Transactional
    public void unblack(Long id) {
        User user = userDao.selectById(id);
        if (user != null) {
            user.setIsBlack(false);
            userDao.updateById(user);
            UserSta userSta = userStaDao.selectById(id);
            userSta.setIsBlack(false);
            userStaDao.updateById(userSta);
        }
    }

    @Transactional
    public void generateSubordinate(GenerateSubordinateRequest req) {
        String parentSymbolCode = null;
        User parent = userDao.selectById(req.getParentId());
        Long randomRegisterTime = 0L;
        LocalDateTime parentRegisterTime = req.getStartTime();
        if (parent != null) {
            parentSymbolCode = parent.getSymbolCode();
            if (parentRegisterTime == null) {
                parentRegisterTime = parent.getGmtCreate();
            }
            if (req.getEndTime() != null) {
                randomRegisterTime = TimeUtil.getTimeMillis(req.getEndTime())
                        - TimeUtil.getTimeMillis(parentRegisterTime);
            } else {
                randomRegisterTime = System.currentTimeMillis() - TimeUtil.getTimeMillis(parentRegisterTime);
            }
        }
        randomRegisterTime = randomRegisterTime / 1000;
        List<Long> uidList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < req.getNum(); i++) {
            // 生成账号
            String username = RandomStringUtils.randomAlphanumeric(10) + "@gmail.com";
            String pwd = "1234569";
            Integer groupIndex = getGroupIndex();
            // 创建用户
            User user = new User();
            user.setId(idWorker.nextId());
            user.setUsername(username);
            user.setRegisterType(RegisterEnum.EMAIL);
            user.setEmail(username);
            user.setAuthorityType(AuthorityEnum.CLIENT);
            user.setRegisterPlatform(PlatformEnum.H5);
            user.setLoginPassword(passwordEncoder.encode(pwd));
            if (parent != null) {
                user.setRegisterIp(parent.getRegisterIp());
            } else {
                user.setRegisterIp("127.0.0.1");
            }
            user.setSource(1);
            user.setUid(NumberUtil.generateCode(8));
            user.setNickname(username);
            user.setSymbol(createSymbol(parentSymbolCode, user));
            user.setSymbolCode(NumberUtil.generateCode(8));
            user.setGroupIndex(groupIndex);
            user.setIsVip(false);
            user.setIsBlack(false);
            user.setIsReal(false);
            if (parentRegisterTime != null && randomRegisterTime > 0) {
                LocalDateTime registerTime = parentRegisterTime
                        .plusSeconds(random.nextInt(randomRegisterTime.intValue()));
                user.setGmtCreate(registerTime);
                user.setGmtUpdate(registerTime);
            }
            userDao.insert(user);
            UserSta userSta = new UserSta();
            userSta.setId(user.getId());
            userSta.setUsername(user.getUsername());
            userSta.setParentId(user.getParentId());
            userSta.setTotalRechargeAmount(BigDecimal.ZERO);
            userSta.setTotalWithdrawAmount(BigDecimal.ZERO);
            userSta.setInviteCount(0);
            userSta.setInviteRechargeCount(0);
            userSta.setIsBlack(false);
            userSta.setIsGenerate(false);
            userSta.setIsReal(false);
            userStaDao.insert(userSta);
            createUserAccount(user);
            redisTemplate.opsForValue().set(RedisKey.OPTION_USER_GROUP_KEY, groupIndex);
            uidList.add(user.getId());
        }
        for (Long uid : uidList) {
            registerConsumer(uid);
        }

    }

    @Transactional
    public void updateStarLevel(Long userId, Integer starLevel) {
        User user = userDao.selectById(userId);
        if (user != null) {
            if (user.getStarLevel() == null || user.getStarLevel().intValue() < starLevel.intValue()) {
                user.setStarLevel(starLevel);
                userDao.updateById(user);
            }
        }
    }

    public UserCountDTO newsta(Long userId) throws ExecutionException {
        UserCountDTO result = new UserCountDTO();
        result.setPersonCount(0);
        result.setPendingPersonCount(0);
        result.setTotalAmount(BigDecimal.ZERO);
        List<Map<String, Object>> list = userDao.queryUserCount(userId);
        BigDecimal inviteIncome = accountService.queryLevelCount(userId);
        if (null != list && !list.isEmpty()) {
            result.setTotalAmount(inviteIncome);
            list.stream().forEach(item -> {
                if (StringUtils.equalsIgnoreCase(InviteAuditStatusEnum.PASS.toString(), null != item.get(
                        "inviteAuditStatus") ? item.get("inviteAuditStatus").toString() : "")) {
                } else {
                    result.setPendingPersonCount(result.getPendingPersonCount() + 1);
                }
            });
            result.setPersonCount(list.size());
        }
        return result;
    }

    public UserStaDTO sta(Long userId, int level) throws ExecutionException {
        long timeMillis = System.currentTimeMillis();
        if (0 == level) {
            return sta(userId);
        }
        final UserStaDTO result = new UserStaDTO();
        result.setPersonCount(0);
        result.setInviteCount(0);
        result.setInviteIncome(BigDecimal.ZERO);
        result.setBalance(BigDecimal.ZERO);
        result.setFreezeCapital(BigDecimal.ZERO);
        BigDecimal inviteIncome = accountService.queryLevel(userId, level);
        result.setInviteIncome(inviteIncome);
        List<UserTreeDTO> userTreeDTOS = userDao.queryUserId(userId, level);
        if (null != userTreeDTOS && !userTreeDTOS.isEmpty()) {
            userTreeDTOS.stream().forEach(item -> {
                result.setBalance(result.getBalance().add(item.getBalance()));
                result.setFreezeCapital(result.getFreezeCapital().add(item.getFreezeCapital()));
            });
            result.setInviteCount(userTreeDTOS.size());
            result.setPersonCount(userTreeDTOS.size());

        }
        log.info("查询分级用户统计耗时:{}ms", System.currentTimeMillis() - timeMillis);
        return result;
    }

    public UserStaDTO sta(Long userId) {
        UserStaDTO result = new UserStaDTO();
        UserSta userSta = userStaDao.selectById(userId);
        result.setInviteCount(null != userSta ? userSta.getInviteCount() : 0);

        User user = userDao.selectById(userId);
        if (user != null && user.getGroupIndex() != null) {
            result.setInviteIncome(queryIncomeTeam(userId, user.getGroupIndex()));
        } else {
            result.setInviteIncome(BigDecimal.ZERO);
        }

        UserAccountDTO account = accountService.queryAccount(userId);
        if (account != null) {
            result.setBalance(account.getBalance());
            result.setFreezeCapital(account.getFreezeCapital());
        } else {
            result.setBalance(BigDecimal.ZERO);
            result.setFreezeCapital(BigDecimal.ZERO);
        }
        return result;
    }

    public void sta(UserInviteTreeDTO inviteTreeDTO) {
        QueryWrapper<AccountStatement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AccountStatement.TYPE, TransactionEnum.CREDIT_INVITE_WAGER)
                .eq(AccountStatement.USER_ID, inviteTreeDTO.getId());
        List<UserAccountStatementDTO> list = accountStatementService.selectList(queryWrapper,
                inviteTreeDTO.getGroupIndex());
        BigDecimal inviteAmount = BigDecimal.ZERO;
        if (!CollectionUtils.isEmpty(list)) {
            for (UserAccountStatementDTO accountStatement : list) {
                inviteAmount = inviteAmount.add(accountStatement.getAmount());
            }
        }
        inviteTreeDTO.setTotalContribution(inviteAmount);
    }

    public BigDecimal sta(UserTreeDTO userTreeDTO, int level) {
        return accountStatementService.querySta(userTreeDTO.getId(), userTreeDTO.getGroupIndex(), level);
    }

    public BigDecimal sta(Long userId, Integer groupIndex, int level) {
        return accountStatementService.querySta(userId, groupIndex, level);
    }

    /**
     * 查询用户Map
     * <p>
     * key为用户ID，value为用户
     * </p>
     */
    private Map<Long, User> userMap() {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.select(User.ID, User.PARENT_ID, User.GROUP_INDEX);
        query.eq(User.AUTHORITY_TYPE, AuthorityEnum.CLIENT);
        List<User> list = userDao.selectList(query);
        Map<Long, User> result = new HashMap<>();
        for (User user : list) {
            result.put(user.getId(), user);
        }
        return result;
    }


    /**
     * 查询用户邀请人
     * @param queryUserInvitePeopleDTO
     * @return
     */
    public UserInvitePeopleDTO queryUserInvitePeople(QueryUserInvitePeopleDTO queryUserInvitePeopleDTO){
        log.info("queryUserInvitePeople 开始 queryUserInvitePeopleDTO={}",queryUserInvitePeopleDTO);
        LambdaQueryWrapper<User> qw = Wrappers.lambdaQuery();
        if(StringUtils.isNotBlank(queryUserInvitePeopleDTO.getSymbolCode())){
            qw.eq(User::getSymbolCode, queryUserInvitePeopleDTO.getSymbolCode());
        }else if(StringUtils.isNotBlank(queryUserInvitePeopleDTO.getUid())){
            qw.eq(User::getUid, queryUserInvitePeopleDTO.getUid());
        }else {
            return null;
        }

        User user = userDao.selectOne(qw);
        if(user == null){
            return null;
        }
        UserInvitePeopleDTO userInvitePeopleDTO = new UserInvitePeopleDTO();
        BeanUtils.copyProperties(user,userInvitePeopleDTO);
        try {
            if (queryUserInvitePeopleDTO.getUp().equals(1)){
                userInvitePeopleDTO.setParentId(user.getParentId());
                getChildParent(userInvitePeopleDTO);
            }else {
                getChildSymbolCode(userInvitePeopleDTO);
            }
            log.info("queryUserInvitePeople 结束 userDTO={}", JSON.toJSON(userInvitePeopleDTO));
            return userInvitePeopleDTO;
        } catch (Exception e) {
            log.error("queryUserInvitePeople 异常 queryUserInvitePeopleDTO={} e=", queryUserInvitePeopleDTO,e);
            return null;
        }
    }


    private void getChildSymbolCode(UserInvitePeopleDTO userInvitePeopleDTO){
        if(userInvitePeopleDTO == null){
            return;
        }
        LambdaQueryWrapper<User> qw = Wrappers.lambdaQuery();

        qw.eq(User::getParentSymbolCode, userInvitePeopleDTO.getSymbolCode());
        List<User> userList = userDao.selectList(qw);
        if(CollectionUtils.isEmpty(userList)){
            return;
        }
        ArrayList<UserInvitePeopleDTO> userDTOList = new ArrayList<>();
        for (User userPO : userList) {
            //查询金额
            PaymentOrder paymentOrder = paymentOrderService.queryUserInvitePeopleAmount(userPO.getId());
            UserInvitePeopleDTO dto = new UserInvitePeopleDTO();
            BeanUtils.copyProperties(userPO,dto);
            dto.setReqNumSum(paymentOrder.getReqNumSum());
            dto.setReqMoneySum(paymentOrder.getReqMoneySum());
            dto.setRealMoneySum(paymentOrder.getRealMoneySum());
            dto.setRealNumSum(paymentOrder.getRealNumSum());

            List<WithdrawAmountDTO> withdrawAmountDTOList = withdrawOrderService.totalWithdrawAmountByUsers(Collections.singletonList(userPO.getId()));
            if(!CollectionUtils.isEmpty(withdrawAmountDTOList) && withdrawAmountDTOList.get(0).getAmount() != null){
                dto.setWithdrawTotalAmount(withdrawAmountDTOList.get(0).getAmount());
            }
            userDTOList.add(dto);
            getChildSymbolCode(dto);
        }

        BigDecimal reqNumSum = BigDecimal.ZERO;
        BigDecimal reqMoneySum = BigDecimal.ZERO;
        BigDecimal realMoneySum = BigDecimal.ZERO;
        BigDecimal realNumSum = BigDecimal.ZERO;
        BigDecimal withdrawTotalAmount = BigDecimal.ZERO;
        for (UserInvitePeopleDTO invitePeopleDTO : userDTOList) {
            reqNumSum = reqNumSum.add(invitePeopleDTO.getReqNumSum() == null?BigDecimal.ZERO:invitePeopleDTO.getReqNumSum());
            reqMoneySum = reqMoneySum.add(invitePeopleDTO.getReqMoneySum() == null?BigDecimal.ZERO:invitePeopleDTO.getReqMoneySum());
            realMoneySum = realMoneySum.add(invitePeopleDTO.getRealMoneySum() == null?BigDecimal.ZERO:invitePeopleDTO.getRealMoneySum());
            realNumSum = realNumSum.add(invitePeopleDTO.getRealNumSum() == null?BigDecimal.ZERO:invitePeopleDTO.getRealNumSum());
            withdrawTotalAmount = withdrawTotalAmount.add(invitePeopleDTO.getWithdrawTotalAmount() == null?BigDecimal.ZERO:invitePeopleDTO.getWithdrawTotalAmount());
        }
        userInvitePeopleDTO.setChildUserDTOList(userDTOList);
        userInvitePeopleDTO.setSymbolCount(userDTOList.size());


        BigDecimal reqNumSumParent = userInvitePeopleDTO.getReqNumSum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getReqNumSum();
        BigDecimal reqMoneySumParent = userInvitePeopleDTO.getReqMoneySum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getReqMoneySum();
        BigDecimal realMoneySumParent = userInvitePeopleDTO.getRealMoneySum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getRealMoneySum();
        BigDecimal realNumSumParent = userInvitePeopleDTO.getRealNumSum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getRealNumSum();
        BigDecimal withdrawTotalAmountParent = userInvitePeopleDTO.getWithdrawTotalAmount() == null?BigDecimal.ZERO:userInvitePeopleDTO.getWithdrawTotalAmount();

        userInvitePeopleDTO.setReqNumSum(reqNumSumParent.add(reqNumSum));
        userInvitePeopleDTO.setReqMoneySum(reqMoneySumParent.add(reqMoneySum));
        userInvitePeopleDTO.setRealMoneySum(realMoneySumParent.add(realMoneySum));
        userInvitePeopleDTO.setRealNumSum(realNumSumParent.add(realNumSum));
        userInvitePeopleDTO.setRealNumSum(withdrawTotalAmountParent.add(withdrawTotalAmount));
    }

    private void getChildParent(UserInvitePeopleDTO userInvitePeopleDTO){
        if(userInvitePeopleDTO == null || userInvitePeopleDTO.getParentId() == null){
            return;
        }
        LambdaQueryWrapper<User> qw = Wrappers.lambdaQuery();

        qw.eq(User::getUid, userInvitePeopleDTO.getParentId());
        List<User> userList = userDao.selectList(qw);
        if(CollectionUtils.isEmpty(userList)){
            return;
        }
        ArrayList<UserInvitePeopleDTO> userDTOList = new ArrayList<>();
        for (User userPO : userList) {
            //查询金额
            PaymentOrder paymentOrder = paymentOrderService.queryUserInvitePeopleAmount(userPO.getId());
            UserInvitePeopleDTO dto = new UserInvitePeopleDTO();
            BeanUtils.copyProperties(userPO,dto);
            dto.setReqNumSum(paymentOrder.getReqNumSum());
            dto.setReqMoneySum(paymentOrder.getReqMoneySum());
            dto.setRealMoneySum(paymentOrder.getRealMoneySum());
            dto.setRealNumSum(paymentOrder.getRealNumSum());
            dto.setParentId(userPO.getParentId());

            List<WithdrawAmountDTO> withdrawAmountDTOList = withdrawOrderService.totalWithdrawAmountByUsers(Collections.singletonList(userPO.getId()));
            if(!CollectionUtils.isEmpty(withdrawAmountDTOList) && withdrawAmountDTOList.get(0).getAmount() != null){
                dto.setWithdrawTotalAmount(withdrawAmountDTOList.get(0).getAmount());
            }
            userDTOList.add(dto);
            getChildParent(dto);
        }

        BigDecimal reqNumSum = BigDecimal.ZERO;
        BigDecimal reqMoneySum = BigDecimal.ZERO;
        BigDecimal realMoneySum = BigDecimal.ZERO;
        BigDecimal realNumSum = BigDecimal.ZERO;
        BigDecimal withdrawTotalAmount = BigDecimal.ZERO;
        for (UserInvitePeopleDTO invitePeopleDTO : userDTOList) {
            reqNumSum = reqNumSum.add(invitePeopleDTO.getReqNumSum() == null?BigDecimal.ZERO:invitePeopleDTO.getReqNumSum());
            reqMoneySum = reqMoneySum.add(invitePeopleDTO.getReqMoneySum() == null?BigDecimal.ZERO:invitePeopleDTO.getReqMoneySum());
            realMoneySum = realMoneySum.add(invitePeopleDTO.getRealMoneySum() == null?BigDecimal.ZERO:invitePeopleDTO.getRealMoneySum());
            realNumSum = realNumSum.add(invitePeopleDTO.getRealNumSum() == null?BigDecimal.ZERO:invitePeopleDTO.getRealNumSum());
            withdrawTotalAmount = withdrawTotalAmount.add(invitePeopleDTO.getWithdrawTotalAmount() == null?BigDecimal.ZERO:invitePeopleDTO.getWithdrawTotalAmount());
        }
        userInvitePeopleDTO.setChildUserDTOList(userDTOList);
        userInvitePeopleDTO.setSymbolCount(userDTOList.size());


        BigDecimal reqNumSumParent = userInvitePeopleDTO.getReqNumSum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getReqNumSum();
        BigDecimal reqMoneySumParent = userInvitePeopleDTO.getReqMoneySum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getReqMoneySum();
        BigDecimal realMoneySumParent = userInvitePeopleDTO.getRealMoneySum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getRealMoneySum();
        BigDecimal realNumSumParent = userInvitePeopleDTO.getRealNumSum() == null?BigDecimal.ZERO:userInvitePeopleDTO.getRealNumSum();
        BigDecimal withdrawTotalAmountParent = userInvitePeopleDTO.getWithdrawTotalAmount() == null?BigDecimal.ZERO:userInvitePeopleDTO.getWithdrawTotalAmount();

        userInvitePeopleDTO.setReqNumSum(reqNumSumParent.add(reqNumSum));
        userInvitePeopleDTO.setReqMoneySum(reqMoneySumParent.add(reqMoneySum));
        userInvitePeopleDTO.setRealMoneySum(realMoneySumParent.add(realMoneySum));
        userInvitePeopleDTO.setRealNumSum(realNumSumParent.add(realNumSum));
        userInvitePeopleDTO.setRealNumSum(withdrawTotalAmountParent.add(withdrawTotalAmount));
    }

}
