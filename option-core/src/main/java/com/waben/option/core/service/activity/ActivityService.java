package com.waben.option.core.service.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.UserBerealMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.point.PointRunOrderAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.bean.TradeTransactionRemark;
import com.waben.option.common.model.bean.TransactionRemark;
import com.waben.option.common.model.dto.activity.ActivityDTO;
import com.waben.option.common.model.dto.activity.ActivityUserJoinDTO;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import com.waben.option.common.model.dto.user.InviteTaskAuditDTO;
import com.waben.option.common.model.enums.*;
import com.waben.option.common.model.request.user.InviteTaskAuditRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.TimeUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.resource.ConfigService;
import com.waben.option.data.entity.activity.Activity;
import com.waben.option.data.entity.activity.ActivityUserJoin;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.activity.ActivityDao;
import com.waben.option.data.repository.activity.ActivityUserJoinDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserStaDao;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ActivityDao activityDao;

    @Resource
    private ActivityUserJoinDao activityUserJoinDao;

    @Resource
    private UserDao userDao;

    @Resource
    private UserStaDao userStaDao;

    @Resource
    private AccountService accountService;

    @Resource
    private ConfigService configService;

    @Resource
    private AMQPService amqpService;

    @Resource
    private PointRunOrderAPI pointRunOrderAPI;

    @Resource
    private StaticConfig staticConfig;

    private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ActivityUserJoinDTO> joinStatusList(Long userId, ActivityTypeEnum type) {
        String day = LocalDate.now().format(dayFormatter);
        List<ActivityUserJoin> joinList = new ArrayList<>();
        Map<ActivityTypeEnum, Long> activityJoinIntervalMap = new HashMap<>();
        // ???????????????
        List<ActivityTypeEnum> dailyLimitTypeList = new ArrayList<>();
        List<ActivityTypeEnum> otherTypeList = new ArrayList<>();
        if (type == null) {
            List<Activity> activityList = queryEnableActivityList();
            for (Activity activity : activityList) {
                if (activity.getJoinLimit() == ActivityJoinLimitEnum.DAILY_LIMIT) {
                    dailyLimitTypeList.add(activity.getType());
                } else {
                    otherTypeList.add(activity.getType());
                }
                activityJoinIntervalMap.put(activity.getType(), activity.getJoinTimeInterval());
            }
        } else {
            ActivityDTO activity = queryActivity(type);
            if (activity.getJoinLimit() == ActivityJoinLimitEnum.DAILY_LIMIT) {
                dailyLimitTypeList.add(activity.getType());
            } else {
                otherTypeList.add(activity.getType());
            }
            activityJoinIntervalMap.put(activity.getType(), activity.getJoinTimeInterval());
        }
        // ??????DAILY_LIMIT??????????????????
        boolean yesterdaySignData = false;
        if (dailyLimitTypeList.size() > 0) {
            QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
            query.eq(ActivityUserJoin.USER_ID, userId);
            query.in(ActivityUserJoin.ACTIVITY_TYPE, dailyLimitTypeList);
            query.eq(ActivityUserJoin.DAY, day);
            List<ActivityUserJoin> dailyOneJoinList = activityUserJoinDao.selectList(query);
            joinList.addAll(dailyOneJoinList);
            // ??????????????????
            boolean hasSign = false;
            for (ActivityUserJoin join : dailyOneJoinList) {
                if (join.getActivityType() == ActivityTypeEnum.SIGN_IN) {
                    hasSign = true;
                    break;
                }
            }
            if (!hasSign) {
                // ????????????????????????????????????????????????????????????
                String yesterday = LocalDate.now().minusDays(1).format(dayFormatter);
                query = new QueryWrapper<>();
                query.eq(ActivityUserJoin.USER_ID, userId);
                query.eq(ActivityUserJoin.ACTIVITY_TYPE, ActivityTypeEnum.SIGN_IN.name());
                query.eq(ActivityUserJoin.DAY, yesterday);
                List<ActivityUserJoin> yesterdaySignList = activityUserJoinDao.selectList(query);
                if (yesterdaySignList != null && yesterdaySignList.size() > 0) {
                    ActivityUserJoin yesterdaySign = yesterdaySignList.get(yesterdaySignList.size() - 1);
                    if (yesterdaySign.getContinueDays() < 7) {
                        yesterdaySignData = true;
                        joinList.add(yesterdaySign);
                    }
                }
            }
        }
        // ??????FOREVER_ONE???LIMIT_NO??????????????????
        if (otherTypeList.size() > 0) {
            QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
            query = new QueryWrapper<>();
            query.eq(ActivityUserJoin.USER_ID, userId);
            query.in(ActivityUserJoin.ACTIVITY_TYPE, otherTypeList);
            List<ActivityUserJoin> otherJoinList = activityUserJoinDao.selectList(query);
            joinList.addAll(otherJoinList);
        }
        // ?????????dto
        final boolean yesterdaySignDataFinal = yesterdaySignData;
        List<ActivityUserJoinDTO> result = joinList.stream().map(temp -> {
            ActivityUserJoinDTO dto = modelMapper.map(temp, ActivityUserJoinDTO.class);
            if (yesterdaySignDataFinal && temp.getActivityType() == ActivityTypeEnum.SIGN_IN) {
                dto.setYesterdaySignData(true);
            }
            return dto;
        }).collect(Collectors.toList());
        // ??????????????????????????????
        if (result != null && result.size() > 0) {
            for (ActivityUserJoinDTO dto : result) {
                dto.setJoinTimeInterval(0L);
                dto.setNextJoinTime(0L);
                Long joinTimeInterval = activityJoinIntervalMap.get(dto.getActivityType());
                if (joinTimeInterval != null && joinTimeInterval.longValue() > 0) {
                    dto.setJoinTimeInterval(joinTimeInterval);
                    if (dto.getReceiveTime() != null) {
                        LocalDateTime nextJoinTime = dto.getReceiveTime().plusMinutes(joinTimeInterval);
                        if (nextJoinTime.isAfter(LocalDateTime.now())) {
                            dto.setNextJoinTime(TimeUtil.getTimeMillis(nextJoinTime));
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<Activity> queryEnableActivityList() {
        QueryWrapper<Activity> query = new QueryWrapper<>();
        query.eq(Activity.ENABLE, true);
        return activityDao.selectList(query);
    }

    public ActivityDTO queryActivity(ActivityTypeEnum type) {
        QueryWrapper<Activity> query = new QueryWrapper<>();
        query.eq(Activity.TYPE, type);
        Activity entity = activityDao.selectOne(query);
        if (entity != null) {
            return modelMapper.map(entity, ActivityDTO.class);
        } else {
            return null;
        }
    }

    @Transactional
    public BigDecimal receive(Long userId, ActivityTypeEnum type) {
        BigDecimal rewardAmount = null;
        String day = LocalDate.now().format(dayFormatter);
        ActivityDTO activity = queryActivity(type);
        ActivityJoinLimitEnum joinLimit = activity.getJoinLimit();
        BigDecimal rewardCount = BigDecimal.ZERO;
        // ??????????????????
        QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
        query.eq(ActivityUserJoin.USER_ID, userId);
        query.eq(ActivityUserJoin.ACTIVITY_TYPE, type);
        if (joinLimit == ActivityJoinLimitEnum.DAILY_LIMIT) {
            query.eq(ActivityUserJoin.DAY, day);
        }
        query.orderByDesc(ActivityUserJoin.GMT_CREATE);
        List<ActivityUserJoin> list = activityUserJoinDao.selectList(query);
        ActivityUserJoin join = null;
        if (list != null && list.size() > 0) {
            join = list.get(0);
        }
        if (join != null) {
            if (join.getStatus() == ActivityUserJoinStatusEnum.RECEIVED) {
                // ??????????????????
                if (joinLimit == ActivityJoinLimitEnum.DAILY_LIMIT) {
                    throw new ServerException(5018);
                } else if (joinLimit == ActivityJoinLimitEnum.FOREVER_ONE) {
                    throw new ServerException(5017);
                }
            } else if (join.getStatus() == ActivityUserJoinStatusEnum.WAITING_RECEIVE) {
                // ????????????
                BigDecimal quantity = join.getCurrentQuantity().subtract(join.getReceiveQuantity());
                BigDecimal[] arr = quantity.divideAndRemainder(activity.getReceiveStepQuantity());
                if (joinLimit == ActivityJoinLimitEnum.LIMIT_NO) {
                    rewardCount = arr[0];
                    join.setReceiveQuantity(
                            join.getReceiveQuantity().add(activity.getReceiveStepQuantity().multiply(rewardCount)));
                    join.setStatus(ActivityUserJoinStatusEnum.PROGRESSING);
                } else {
                    BigDecimal receiveQuantity = join.getReceiveQuantity();
                    for (int i = 0; i < arr[0].intValue(); i++) {
                        if (receiveQuantity.compareTo(join.getTargetQuantity()) >= 0) {
                            break;
                        }
                        receiveQuantity = receiveQuantity.add(activity.getReceiveStepQuantity());
                        rewardCount = rewardCount.add(BigDecimal.ONE);
                    }
                    join.setReceiveQuantity(receiveQuantity);
                    if (receiveQuantity.compareTo(join.getTargetQuantity()) >= 0) {
                        join.setReceiveTime(LocalDateTime.now());
                        join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
                    } else {
                        join.setStatus(ActivityUserJoinStatusEnum.PROGRESSING);
                    }
                }
                if (rewardCount.compareTo(BigDecimal.ZERO) > 0) {
                    join.setReceiveTime(LocalDateTime.now());
                }
                activityUserJoinDao.updateById(join);
            }
        } else if (type == ActivityTypeEnum.SIGN_IN) {
            Integer continueDays = 0;
            // ??????????????????????????????
            String yesterday = LocalDate.now().minusDays(1).format(dayFormatter);
            query = new QueryWrapper<>();
            query.eq(ActivityUserJoin.USER_ID, userId);
            query.eq(ActivityUserJoin.ACTIVITY_TYPE, ActivityTypeEnum.SIGN_IN.name());
            query.eq(ActivityUserJoin.DAY, yesterday);
            List<ActivityUserJoin> yesterdaySignList = activityUserJoinDao.selectList(query);
            if (yesterdaySignList != null && yesterdaySignList.size() > 0) {
                ActivityUserJoin yesterdaySign = yesterdaySignList.get(yesterdaySignList.size() - 1);
                if (yesterdaySign.getContinueDays() < 7) {
                    continueDays = yesterdaySign.getContinueDays();
                }
            }
            // ???????????????????????????
            join = new ActivityUserJoin();
            join.setId(idWorker.nextId());
            join.setUserId(userId);
            join.setActivityType(type);
            join.setDay(day);
            join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
            join.setCurrentQuantity(activity.getTargetQuantity());
            join.setTargetQuantity(activity.getTargetQuantity());
            join.setReceiveQuantity(activity.getTargetQuantity());
            join.setReceiveTime(LocalDateTime.now());
            join.setLastWaitingReceiveTime(join.getReceiveTime());
            join.setContinueDays(continueDays + 1);
            activityUserJoinDao.insert(join);
            rewardCount = activity.getTargetQuantity();
            if (join.getContinueDays() >= 7) {
                // ????????????7?????????7000
                activity.setRewardAmount(new BigDecimal(7000));
            }
        }
        // ????????????
        if (join != null && rewardCount.compareTo(BigDecimal.ZERO) > 0 && !staticConfig.isContract()
                && isShareActivity(type)) {
            shareAward(userId);
        } else {
            if (join != null && rewardCount.compareTo(BigDecimal.ZERO) > 0) {
                createActivityFlow(join.getUserId(), type, join.getId(),
                        activity.getRewardAmount().multiply(rewardCount));
                rewardAmount = activity.getRewardAmount().multiply(rewardCount);
            }
        }
        return rewardAmount;
    }

    @Transactional
    private void toRealInvite(Long userId) {
        // ????????????????????????????????????
        UserSta userSta = userStaDao.selectById(userId);
//		LocalDateTime now = LocalDateTime.now();
//		if (!userSta.getHasFirstShare() && now.isBefore(userSta.getGmtCreate().plusDays(1))) {
//			userSta.setHasFirstShare(true);
//			userStaDao.updateById(userSta);
//			if (userSta.getHasFirstLogin() && !userSta.getIsReal()) {
//				beRealProducer(userId);
//			}
//		}
        if (!userSta.getIsReal()) {
            beRealProducer(userId);
        }
    }

    @Transactional
    private void shareAward(Long userId) {
        // ??????????????????
        pointRunOrderAPI.gift(userId);
    }

    private void beRealProducer(Long userId) {
        amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_BEREAL,
                new AMQPMessage<UserBerealMessage>(new UserBerealMessage(userId)));
    }

    private boolean isShareActivity(ActivityTypeEnum type) {
        boolean result = false;
        switch (type) {
            case SHARECHAT:
            case MOJ:
            case DISCORD:
//		case FACE_BOOK:
            case SNAPCHAT:
//		case WHATS_APP:
//		case YOUTUBE:
//		case SUNSHINE_PROFIT:
            case ZALO:
            case INSTAGRAM:
            case MESSAGE:
            case TWITTER:
//		case TELEGRAM:
//		case TIKTOK:
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    @SuppressWarnings("incomplete-switch")
    @Transactional
    private List<Long> createActivityFlow(Long userId, ActivityTypeEnum activityType, Long joinId, BigDecimal amount) {
        List<Long> ids = new LinkedList<>();
        List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
        switch (activityType) {
            case SIGN_IN:
                transactionBeanList
                        .add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.CREDIT_LOGIN_PROFIT)
                                .amount(amount).transactionId(joinId).currency(staticConfig.getDefaultCurrency()).build());
                break;
            case INVITE:
//                transactionBeanList
//                        .add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.CREDIT_INVITE_REGISTER)
//                                .amount(amount).transactionId(joinId).currency(staticConfig.getDefaultCurrency()).build());
                break;
            case INVESTMENT:
                transactionBeanList
                        .add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.CREDIT_ACTIVITY_WAGER)
                                .amount(amount).transactionId(joinId).currency(staticConfig.getDefaultCurrency()).build());
                break;
            case SUNSHINE:
            case SHARECHAT:
            case MOJ:
            case DISCORD:
            case FACE_BOOK:
            case SNAPCHAT:
            case WHATS_APP:
            case YOUTUBE:
            case ZALO:
            case INSTAGRAM:
            case MESSAGE:
            case TWITTER:
            case TELEGRAM:
            case TIKTOK:
            case SHARE_MUL_GROUP:
            case SUNSHINE_PROFIT:
            case SUNSHINE_RECHARGE:
            case SUNSHINE_WITHDRAWAL:
            case JOIN_TG_GROUP:
            case SUB_TG_CHANNEL:
            case SHARE_ORDER:
            case SHARE_JOB:
                transactionBeanList
                        .add(AccountTransactionBean.builder().userId(userId).type(TransactionEnum.CREDIT_SUNSHINE)
                                .amount(amount).transactionId(joinId).currency(staticConfig.getDefaultCurrency())
                                .remark(buildTransactionRemark(activityType.name())).build());
                break;
        }
        if (!CollectionUtils.isEmpty(transactionBeanList)) {
            ids = accountService.transaction(userId, transactionBeanList);
        }
        return ids;
    }

    private String buildTransactionRemark(String name) {
        TransactionRemark<TradeTransactionRemark> remark = new TransactionRemark<>(TransactionRemark.RemarkEnum.TRADE,
                TradeTransactionRemark.builder().args(name).build());
        return remark.toString();
    }

    @Transactional
    public void updateJoin(UpdateJoinDTO req) {
        String day = LocalDate.now().format(dayFormatter);
        ActivityDTO activity = queryActivity(req.getType());
        List<Long> ids = new LinkedList<>();
        if (req.getType() == ActivityTypeEnum.INVESTMENT) {
            // ???????????????????????????????????????????????????VIP??????
            User user = userDao.selectById(req.getUserId());
            if (user.getIsVip() == null || !user.getIsVip()) {
                user.setIsVip(true);
                userDao.updateById(user);
            }
            // ??????????????????????????????
            Integer checkCount = accountService.queryStatementCount(req.getUserId(),
                    TransactionEnum.CREDIT_INVESTMENT_GIFT);
            if (checkCount == null || checkCount.intValue() <= 0) {
                ConfigDTO giftConfig = configService.queryConfig(DBConstants.INVESTMENT_GIFT);
                if (giftConfig != null && !StringUtils.isBlank(giftConfig.getValue())) {
                    BigDecimal gift = new BigDecimal(giftConfig.getValue().trim());
                    if (gift.compareTo(BigDecimal.ZERO) > 0) {
                        List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                        transactionBeanList.add(AccountTransactionBean.builder().userId(req.getUserId())
                                .type(TransactionEnum.CREDIT_INVESTMENT_GIFT).amount(gift)
                                .transactionId(req.getOrderId()).currency(staticConfig.getDefaultCurrency()).build());
                        ids = accountService.transaction(req.getUserId(), transactionBeanList);
                    }
                }
            }
        }

        if (activity.getEnable() == null || !activity.getEnable()) {
            return;
        }
        // ??????????????????
        QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
        query.eq(ActivityUserJoin.USER_ID, req.getUserId());
        query.eq(ActivityUserJoin.JOIN_USER_ID, req.getJoinUserId());
        query.eq(ActivityUserJoin.ACTIVITY_TYPE, req.getType());
        if (activity.getJoinLimit() == ActivityJoinLimitEnum.DAILY_LIMIT) {
            query.eq(ActivityUserJoin.DAY, day);
        }
        query.orderByDesc(ActivityUserJoin.GMT_CREATE);
        List<ActivityUserJoin> list = activityUserJoinDao.selectList(query);
        ActivityUserJoin join = null;
        //??????????????????????????????????????????????????? 2022-04-16
//        if (list != null && list.size() > 0) {
//            join = list.get(0);
//            return;
//        }
//		// ??????????????????????????????
//		if (join == null) {
        join = new ActivityUserJoin();
        join.setId(idWorker.nextId());
        join.setJoinUserId(req.getJoinUserId());
        if (null != ids && !ids.isEmpty()) {
            join.setStatementId(ids.get(0));
        }
        join.setUserId(req.getUserId());
        join.setActivityType(req.getType());
        if (activity.getJoinLimit() == ActivityJoinLimitEnum.DAILY_LIMIT) {
            join.setDay(day);
        }
        join.setCurrentQuantity(req.getQuantity());
        join.setTargetQuantity(activity.getTargetQuantity());
        join.setReceiveQuantity(BigDecimal.ZERO);
        if (join.getCurrentQuantity().subtract(join.getReceiveQuantity())
                .compareTo(activity.getReceiveStepQuantity()) >= 0) {
            join.setLastWaitingReceiveTime(LocalDateTime.now());
            join.setStatus(ActivityUserJoinStatusEnum.WAITING_RECEIVE);
        } else {
            join.setStatus(ActivityUserJoinStatusEnum.PROGRESSING);
        }
        if (req.getType() == ActivityTypeEnum.INVITE) {
            join.setInviteAuditStatus(InviteAuditStatusEnum.PENDING);
        }
        activityUserJoinDao.insert(join);
//		} else {
//			join.setCurrentQuantity(join.getCurrentQuantity().add(req.getQuantity()));
//			if (activity.getJoinLimit() != ActivityJoinLimitEnum.LIMIT_NO
//					&& join.getTargetQuantity().compareTo(BigDecimal.ZERO) > 0
//					&& join.getReceiveQuantity().compareTo(join.getTargetQuantity()) >= 0) {
//				join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
//			} else if (join.getCurrentQuantity().subtract(join.getReceiveQuantity())
//					.compareTo(activity.getReceiveStepQuantity()) >= 0) {
//				join.setLastWaitingReceiveTime(LocalDateTime.now());
//				join.setStatus(ActivityUserJoinStatusEnum.WAITING_RECEIVE);
//			} else {
//				join.setStatus(ActivityUserJoinStatusEnum.PROGRESSING);
//			}
//			activityUserJoinDao.updateById(join);
//		}
        // ???????????????????????????
        if (req.getType() == ActivityTypeEnum.INVITE) {
            if (join.getReceiveQuantity().compareTo(join.getTargetQuantity()) < 0) {
                join.setReceiveQuantity(join.getReceiveQuantity().add(req.getQuantity()));
                join.setReceiveTime(LocalDateTime.now());
                join.setLastWaitingReceiveTime(join.getReceiveTime());
                if (join.getReceiveQuantity().compareTo(join.getTargetQuantity()) >= 0) {
                    join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
                } else {
                    join.setStatus(ActivityUserJoinStatusEnum.PROGRESSING);
                }
                if (staticConfig.isContract()) {
                    ids = createActivityFlow(join.getUserId(), join.getActivityType(), join.getId(),
                            activity.getRewardAmount().multiply(req.getQuantity()));
                    if (null != ids && !ids.isEmpty()) {
                        join.setStatementId(ids.get(0));
                    }
                }
                activityUserJoinDao.updateById(join);
            }
        }
        // ?????????????????????????????????????????????
        if (join != null && join.getStatus() == ActivityUserJoinStatusEnum.WAITING_RECEIVE) {
            toRealInvite(join.getUserId());
        }
        // ?????????otc????????????????????????
        if (join != null && !staticConfig.isContract() && isShareActivity(join.getActivityType())
                && join.getStatus() == ActivityUserJoinStatusEnum.WAITING_RECEIVE) {
            // ????????????
            join.setReceiveQuantity(join.getReceiveQuantity().add(BigDecimal.ONE));
            join.setReceiveTime(LocalDateTime.now());
            join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
            activityUserJoinDao.updateById(join);
            shareAward(join.getUserId());
        }
    }

    @Transactional
    public void inviteReceive(String day) {
        ActivityDTO activity = queryActivity(ActivityTypeEnum.INVITE);
        QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
        query.eq(ActivityUserJoin.ACTIVITY_TYPE, ActivityTypeEnum.INVITE);
        query.eq(ActivityUserJoin.DAY, day);
        List<ActivityUserJoin> joinList = activityUserJoinDao.selectList(query);
        for (ActivityUserJoin join : joinList) {
            if (join.getCurrentQuantity().compareTo(join.getReceiveQuantity()) > 0) {
                BigDecimal addQuantity = join.getCurrentQuantity().subtract(join.getReceiveQuantity());
                join.setReceiveQuantity(join.getCurrentQuantity());
                join.setReceiveTime(LocalDateTime.now());
                if (join.getReceiveQuantity().compareTo(join.getTargetQuantity()) >= 0) {
                    join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
                }
                activityUserJoinDao.updateById(join);
                if (staticConfig.isContract()) {
                    createActivityFlow(join.getUserId(), join.getActivityType(), join.getId(),
                            activity.getRewardAmount().multiply(addQuantity));
                }
            }
        }
    }

    private List<Long> blackUserIdList() {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq(User.IS_BLACK, true);
        List<User> list = userDao.selectList(query);
        List<Long> result = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (User u : list) {
                result.add(u.getId());
            }
        }
        return result;
    }

    public PageInfo<InviteTaskAuditDTO> queryAuditInviteList(InviteAuditStatusEnum status, LocalDate day,
                                                             List<Long> uidList, int page, int size) {
        ActivityDTO activity = queryActivity(ActivityTypeEnum.INVITE);
        QueryWrapper<ActivityUserJoin> query = new QueryWrapper<>();
        query.eq(ActivityUserJoin.ACTIVITY_TYPE, ActivityTypeEnum.INVITE);
        query.gt(ActivityUserJoin.CURRENT_QUANTITY, activity.getTargetQuantity());
        if (status != null) {
            query.eq(ActivityUserJoin.INVITE_AUDIT_STATUS, status);
        }
        if (day != null) {
            query.eq(ActivityUserJoin.DAY, day.toString());
        }
        if (!CollectionUtils.isEmpty(uidList)) {
            query.in(ActivityUserJoin.USER_ID, uidList);
        }
        List<Long> blackUserIdList = blackUserIdList();
        if (blackUserIdList != null && blackUserIdList.size() > 0) {
            query.notIn(ActivityUserJoin.USER_ID, blackUserIdList);
        }
        query.orderByDesc(ActivityUserJoin.GMT_CREATE);
        IPage<ActivityUserJoin> pageData = activityUserJoinDao.selectPage(new Page<>(page, size), query);
        // ????????????
        PageInfo<InviteTaskAuditDTO> result = new PageInfo<>();
        if (pageData.getRecords() != null && pageData.getRecords().size() > 0) {
            // ???????????????
            List<Long> tempUidList = new ArrayList<>();
            for (ActivityUserJoin join : pageData.getRecords()) {
                if (!tempUidList.contains(join.getUserId())) {
                    tempUidList.add(join.getUserId());
                }
            }
            Map<Long, String> userIdUsernameMap = userIdUsernameMap(tempUidList);
            // ????????????
            List<InviteTaskAuditDTO> records = new ArrayList<>();
            for (ActivityUserJoin join : pageData.getRecords()) {
                InviteTaskAuditDTO auditDTO = new InviteTaskAuditDTO();
                auditDTO.setActivityType(join.getActivityType());
                auditDTO.setUserCount(join.getCurrentQuantity().intValue());
                auditDTO.setUserId(join.getUserId());
                auditDTO.setStatus(join.getInviteAuditStatus());
                auditDTO.setDay(join.getDay());
                auditDTO.setId(join.getId());
                auditDTO.setUsername(userIdUsernameMap.get(join.getUserId()));
                records.add(auditDTO);
            }
            result.setRecords(records);
        } else {
            result.setRecords(new ArrayList<>());
        }
        result.setPage((int) pageData.getPages());
        result.setSize((int) pageData.getSize());
        result.setTotal(pageData.getTotal());
        return result;
    }

    private Map<Long, String> userIdUsernameMap(List<Long> uidList) {
        Map<Long, String> result = new HashMap<>();
        QueryWrapper<User> query = new QueryWrapper<>();
        query.select(User.ID, User.USERNAME);
        query.in(User.ID, uidList);
        List<User> userList = userDao.selectList(query);
        if (!CollectionUtils.isEmpty(userList)) {
            for (User user : userList) {
                result.put(user.getId(), user.getUsername());
            }
        }
        return result;
    }

    @Transactional
    public void auditInvite(InviteTaskAuditRequest request) {
        ActivityUserJoin join = activityUserJoinDao.selectById(request.getId());
        if (join != null && join.getInviteAuditStatus() == InviteAuditStatusEnum.PENDING) {
            ActivityDTO activity = queryActivity(join.getActivityType());
            if (request.getStatus() == InviteAuditStatusEnum.PASS) {
                BigDecimal number = join.getCurrentQuantity().subtract(join.getReceiveQuantity());
                if (number.compareTo(BigDecimal.ZERO) > 0) {
                    join.setReceiveQuantity(join.getCurrentQuantity());
                    if (join.getCurrentQuantity().compareTo(join.getTargetQuantity()) >= 0
                            && join.getStatus() != ActivityUserJoinStatusEnum.RECEIVED) {
                        join.setStatus(ActivityUserJoinStatusEnum.RECEIVED);
                        join.setReceiveTime(LocalDateTime.now());
                    }
                    List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
                    transactionBeanList.add(AccountTransactionBean.builder().userId(join.getUserId())
                            .type(TransactionEnum.CREDIT_INVITE_REGISTER)
                            .amount(activity.getRewardAmount().multiply(number)).transactionId(join.getId())
                            .currency(staticConfig.getDefaultCurrency()).build());
                    accountService.transaction(join.getUserId(), transactionBeanList);
                }
            }
            join.setInviteAuditStatus(request.getStatus());
            activityUserJoinDao.updateById(join);
        } else {
            throw new ServerException(5010);
        }
    }

    @Transactional
    public void realInviteReceive(Long userId, Long parentId) {
        ActivityDTO activity = queryActivity(ActivityTypeEnum.INVITE);
        createActivityFlow(parentId, ActivityTypeEnum.INVITE, userId, activity.getRewardAmount());
    }

}
