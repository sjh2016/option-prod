package com.waben.option.core.service.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waben.option.common.amqp.AMQPMessage;
import com.waben.option.common.amqp.message.UserBerealMessage;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.SmsAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.payment.PaymentAdminStaDTO;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.dto.push.OutsideMessageDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.enums.OutsidePushMessageType;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.request.payment.PayCoinSuccessRequest;
import com.waben.option.common.model.request.payment.PayOtcSuccessRequest;
import com.waben.option.common.model.request.payment.PaymentAdminPageRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.PaymentUserPageRequest;
import com.waben.option.common.service.AMQPService;
import com.waben.option.common.util.TimeUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.resource.ConfigService;
import com.waben.option.core.service.user.UserService;
import com.waben.option.core.thread.OutsidePushMessageQueue;
import com.waben.option.data.entity.payment.PaymentOrder;
import com.waben.option.data.entity.resource.Level;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.payment.PaymentOrderDao;
import com.waben.option.data.repository.resource.LevelDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserStaDao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@RefreshScope
@Slf4j
@Service
public class PaymentOrderService {

	@Resource
	private UserDao userDao;

	@Resource
	private UserStaDao userStaDao;

	@Resource
	private LevelDao levelDao;

	@Resource
	private PaymentOrderDao paymentOrderDao;

	@Resource
	private ModelMapper modelMapper;

	@Resource
	private IdWorker idWorker;

	@Resource
	private AccountService accountService;

	@Resource
	private OutsidePushMessageQueue outsidePushMessageQueue;

	@Resource
	private UserService userService;

	@Resource
	private PaymentFeeConfigService feeConfigService;

	@Resource
	private ConfigService configService;

	@Resource
	private AMQPService amqpService;

	@Resource
	private SmsAPI smsAPI;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private RedisTemplate<Serializable, Object> redisTemplate;

	@Value("${paymentHiddenValue:-1}")
	private String paymentHiddenValue;

	@Value("${paymentHiddenValueSpec:-1}")
	private String paymentHiddenValueSpec;

	@Value("${paymentGiveRatio:0}")
	private String paymentGiveRatio;

	private Random random = new Random();

	public PaymentOrderDTO query(Long id) {
		PaymentOrder order = paymentOrderDao.selectById(id);
		if (order == null) {
			throw new ServerException(2004);
		} else {
			return modelMapper.map(order, PaymentOrderDTO.class);
		}
	}

	public PaymentOrderDTO last(Long userId) {
		Page<PaymentOrder> page = new Page<>(1, 1);
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.USER_ID, userId);
		query.eq(PaymentOrder.STATUS, PaymentOrderStatusEnum.SUCCESS.name());
		query.orderByDesc(PaymentOrder.ARRIVAL_TIME);
		IPage<PaymentOrder> pageData = paymentOrderDao.selectPage(page, query);
		if (pageData != null && pageData.getRecords() != null && pageData.getRecords().size() > 0) {
			return modelMapper.map(pageData.getRecords().get(0), PaymentOrderDTO.class);
		} else {
			return null;
		}
	}

	public PaymentOrderDTO queryByOrderNo(String orderNo) {
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.ORDER_NO, orderNo);
		PaymentOrder order = paymentOrderDao.selectOne(query);
		if (order == null) {
			throw new ServerException(2004);
		} else {
			return modelMapper.map(order, PaymentOrderDTO.class);
		}
	}

	public PaymentOrderDTO queryByThirdOrderNo(String thirdOrderNo) {
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.THIRD_ORDER_NO, thirdOrderNo);
		PaymentOrder order = paymentOrderDao.selectOne(query);
		if (order == null) {
			throw new ServerException(2004);
		} else {
			return modelMapper.map(order, PaymentOrderDTO.class);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void payOtcSuccess(PayOtcSuccessRequest req) {
		log.info("handle pay otc success, {}", req);
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.ORDER_NO, req.getOrderNo());
		PaymentOrder order = paymentOrderDao.selectOne(query);
		// 更新订单
		if (order.getStatus() == PaymentOrderStatusEnum.PENDING) {
			if (req.getThirdOrderNo() != null) {
				order.setThirdOrderNo(req.getThirdOrderNo());
			}
			order.setStatus(PaymentOrderStatusEnum.SUCCESS);
			order.setRealMoney(req.getRealMoney());
			order.setFee(BigDecimal.ZERO);
			order.setArrivalTime(LocalDateTime.now());
			// 计算到账数量
			BigDecimal realNum = req.getRealMoney().multiply(order.getExchangeRate())
					.setScale(order.getTargetCurrency().getPrecision(), RoundingMode.DOWN);
			order.setRealNum(realNum);
			order.setFee(BigDecimal.ZERO);
			// 计算隐藏
			int hiddenRandom = random.nextInt(100);
			int paymentHidden = -1;
			if (order.getRealNum().compareTo(new BigDecimal(1000000)) >= 0
					&& order.getRealNum().compareTo(new BigDecimal(4000000)) <= 0) {
				paymentHidden = Integer.parseInt(paymentHiddenValueSpec);
			} else {
				paymentHidden = Integer.parseInt(paymentHiddenValue);
			}
			if (hiddenRandom < paymentHidden) {
				order.setIsHidden(true);
			}
			// 更新订单
			paymentOrderDao.updateById(order);
			// 完成支付
			paymentComplete(order);
		}
	}

	@Transactional
	private void modifyUser(Long userId, BigDecimal amount) {
		User user = userDao.selectById(userId);
		UserSta userSta = userStaDao.selectById(userId);
		if (user != null) {
			Integer level = 0;
			BigDecimal totalRechargeAmount = userSta.getTotalRechargeAmount().add(amount);
			List<Level> levelList = levelDao.selectList(new QueryWrapper<Level>().orderByDesc(Level.AMOUNT));
			for (Level le : levelList) {
				if (totalRechargeAmount.compareTo(le.getAmount()) >= 0) {
					level = le.getLevel();
					break;
				}
			}
			user.setLevel(level);
			userDao.updateById(user);
			userSta.setTotalRechargeAmount(totalRechargeAmount);
			if (totalRechargeAmount.compareTo(amount) == 0 && userSta.getParentId() > 0) {
				UserSta parentSta = userStaDao.selectById(userSta.getParentId());
				if (parentSta != null) {
					Integer inviteRechargeCount = parentSta.getInviteRechargeCount();
					if (inviteRechargeCount == null) {
						inviteRechargeCount = 0;
					}
					inviteRechargeCount += 1;
					parentSta.setInviteRechargeCount(inviteRechargeCount);
					userStaDao.updateById(parentSta);
				}
			}
			userStaDao.updateById(userSta);
			if (!userSta.getIsReal()) {
				beRealProducer(userId);
			}
		}
	}

	private void beRealProducer(Long userId) {
		amqpService.convertAndSend(AMQPService.AMQPPublishMode.CONFIRMS, RabbitMessageQueue.QUEUE_USER_BEREAL,
				new AMQPMessage<UserBerealMessage>(new UserBerealMessage(userId)));
	}

	@Transactional(rollbackFor = Exception.class)
	void paymentComplete(PaymentOrder order) {
		// 增加余额
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		BigDecimal amount = order.getRealNum();
		BigDecimal paymentGiveRatioNum = new BigDecimal(paymentGiveRatio);
		if (paymentGiveRatioNum.compareTo(BigDecimal.ZERO) > 0) {
			amount = amount.add(amount.multiply(paymentGiveRatioNum));
		}
		transactionBeanList
				.add(AccountTransactionBean.builder().userId(order.getUserId()).type(TransactionEnum.CREDIT_PAYMENT)
						.amount(amount).transactionId(order.getId()).currency(order.getTargetCurrency()).build());
		accountService.transaction(order.getUserId(), transactionBeanList);
		modifyUser(order.getUserId(), order.getRealNum());
	}

	/**
	 * 站外推送
	 *
	 * @param order
	 */
	@SuppressWarnings("unused")
	private void outsideNotification(PaymentOrder order) {
		OutsideMessageDTO message = new OutsideMessageDTO();
		message.setReferenceId(order.getId());
		message.setType(OutsidePushMessageType.PAYMENT_SUCCESS);
		message.setUserIds(Arrays.asList(order.getUserId()));
		Map<String, String> params = new HashMap<>();
		LocalDateTime time = order.getArrivalTime();
		params.put("time", time.format(TimeUtil.formatter));
		params.put("amount", order.getRealNum().stripTrailingZeros().toPlainString());
		message.setParams(params);
		outsidePushMessageQueue.addMessage(message);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void payCoinSuccess(PayCoinSuccessRequest req) {
		// 再次确认是否已处理
		if (hasThirdOrderNo(req.getThirdOrderNo())) {
			return;
		}
		// 创建订单
		PaymentOrder order = new PaymentOrder();
		Long id = idWorker.nextId();
		order.setId(id);
		order.setUserId(req.getUserId());
		order.setCashType(PaymentCashType.PAYMENT_COIN);
		order.setOrderNo(String.valueOf(id));
		order.setThirdOrderNo(req.getThirdOrderNo());
		order.setStatus(PaymentOrderStatusEnum.SUCCESS);
		order.setReqMoney(req.getRealMoney());
		order.setRealMoney(req.getRealMoney());
		order.setReqCurrency(req.getReqCurrency());
		order.setTargetCurrency(staticConfig.getDefaultCurrency());
		order.setPayApiId(req.getPayApiId());
		order.setPayApiName(req.getPayApiName());
		order.setPayMethodId(req.getPayMethodId());
		order.setPayMethodName(req.getPayMethodName());
		order.setBurseAddress(req.getBurseAddress());
		order.setBurseType(req.getBurseType());
		order.setHash(req.getHash());
		order.setArrivalTime(LocalDateTime.now());
		order.setGmtCreate(order.getArrivalTime());
		// 查询汇率，计算到账数量
		BigDecimal exchangeRate = configService.getUsdtRate();
		BigDecimal realNum = req.getRealMoney().multiply(exchangeRate)
				.setScale(order.getTargetCurrency().getPrecision(), RoundingMode.DOWN);
		order.setExchangeRate(exchangeRate);
		order.setReqNum(realNum);
		order.setRealNum(realNum);
		order.setFee(BigDecimal.ZERO);
		// 计算隐藏
		int hiddenRandom = random.nextInt(100);
		int paymentHidden = -1;
		if (order.getRealNum().compareTo(new BigDecimal(1000000)) >= 0
				&& order.getRealNum().compareTo(new BigDecimal(4000000)) <= 0) {
			paymentHidden = Integer.parseInt(paymentHiddenValueSpec);
		} else {
			paymentHidden = Integer.parseInt(paymentHiddenValue);
		}
		if (hiddenRandom < paymentHidden) {
			order.setIsHidden(true);
		}
		// 插入订单
		paymentOrderDao.insert(order);
		// 完成支付
		paymentComplete(order);
	}

	public boolean hasThirdOrderNo(String thirdOrderNo) {
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.THIRD_ORDER_NO, thirdOrderNo);
		Integer count = paymentOrderDao.selectCount(query);
		if (count != null && count > 0) {
			return true;
		} else {
			return false;
		}
	}

	public PageInfo<PaymentOrderDTO> userPage(Long userId, PaymentUserPageRequest req) {
		Page<PaymentOrder> page = new Page<>(req.getPage(), req.getSize());
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		query.eq(PaymentOrder.USER_ID, userId);
		if (req.getStart() != null) {
			query = query.ge(PaymentOrder.GMT_CREATE, req.getStart());
		}
		if (req.getEnd() != null) {
			query = query.lt(PaymentOrder.GMT_CREATE, req.getEnd());
		}
		if (req.getCashType() != null) {
			query = query.eq(PaymentOrder.CASH_TYPE, req.getCashType());
		}
		if (req.getStatusList() != null && req.getStatusList().size() > 0) {
			query = query.in(PaymentOrder.STATUS, req.getStatusList());
		}
		query = query.orderByDesc(PaymentOrder.GMT_CREATE);
		IPage<PaymentOrder> pageData = paymentOrderDao.selectPage(page, query);
		return new PageInfo<>(pageData.getRecords().stream().map(temp -> modelMapper.map(temp, PaymentOrderDTO.class))
				.collect(Collectors.toList()), pageData.getTotal(), req.getPage(), req.getSize());
	}

	public PageInfo<PaymentOrderDTO> adminPage(PaymentAdminPageRequest req) {
		Page<PaymentOrder> page = new Page<>(req.getPage(), req.getSize());
		QueryWrapper<PaymentOrder> query = new QueryWrapper<>();
		if (req.getCashType() != null) {
			query.eq(PaymentOrder.CASH_TYPE, req.getCashType());
		}
		if (!StringUtils.isBlank(req.getOrderNo())) {
			query.eq(PaymentOrder.ORDER_NO, req.getOrderNo().trim());
		}
		if (!StringUtils.isBlank(req.getThirdOrderNo())) {
			query.eq(PaymentOrder.THIRD_ORDER_NO, req.getThirdOrderNo().trim());
		}
		if (req.getPayApiId() != null) {
			query.eq(PaymentOrder.PAY_API_ID, req.getPayApiId());
			if (req.getPayMethodId() != null) {
				query.eq(PaymentOrder.PAY_METHOD_ID, req.getPayMethodId());
			}
		}
		if (req.getReqCurrency() != null) {
			query.eq(PaymentOrder.REQ_CURRENCY, req.getReqCurrency().name());
		}
		if (req.getStatus() != null) {
			query.eq(PaymentOrder.STATUS, req.getStatus().name());
		}
		if (req.getUidList() != null && req.getUidList().size() > 0) {
			query.in(PaymentOrder.USER_ID, req.getUidList());
		}
		if (StringUtils.isNotBlank(req.getTopId())){
			QueryWrapper<User> queryUser = new QueryWrapper<>();
			queryUser.eq(User.TOP_ID,req.getTopId());
			List<User> users = userDao.selectList(queryUser);
			if (!CollectionUtils.isEmpty(users)){
				List<Long> collect = users.stream().map(User::getId).collect(Collectors.toList());
				query.in(PaymentOrder.USER_ID, collect);
			}
		}
		if (req.getBrokerSymbolList() != null && req.getBrokerSymbolList().size() > 0) {
			query.in(PaymentOrder.BROKER_SYMBOL, req.getBrokerSymbolList());
		}
		if (req.getStartTime() != null) {
			query.ge(PaymentOrder.GMT_CREATE, req.getStartTime());
		}
		if (req.getEndTime() != null) {
			query.lt(PaymentOrder.GMT_CREATE, req.getEndTime());
		}
		if (req.getArrivalStart() != null) {
			query.ge(PaymentOrder.ARRIVAL_TIME, req.getArrivalStart());
		}
		if (req.getArrivalEnd() != null) {
			query.lt(PaymentOrder.ARRIVAL_TIME, req.getArrivalEnd());
		}
		if (req.getIsAll() != null && !req.getIsAll()) {
			query.eq(PaymentOrder.IS_HIDDEN, false);
		}
		query.orderByDesc(PaymentOrder.GMT_CREATE);
		IPage<PaymentOrder> pageData = paymentOrderDao.selectPage(page, query);
		return new PageInfo<>(pageData.getRecords().stream().map(temp -> modelMapper.map(temp, PaymentOrderDTO.class))
				.collect(Collectors.toList()), pageData.getTotal(), req.getPage(), req.getSize());
	}

	public PaymentAdminStaDTO adminSta(PaymentAdminPageRequest req) {
		PaymentAdminStaDTO result = paymentOrderDao.adminSta(req);
		if (result.getRealMoneyTotal() == null) {
			result.setRealMoneyTotal(BigDecimal.ZERO);
		}
		if (result.getRealNumTotal() == null) {
			result.setRealNumTotal(BigDecimal.ZERO);
		}
		if (result.getFeeTotal() == null) {
			result.setFeeTotal(BigDecimal.ZERO);
		}
		return result;
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void close(Long id) {
		PaymentOrder order = paymentOrderDao.selectById(id);
		if (order == null) {
			throw new ServerException(2004);
		}
		if (order.getStatus() == PaymentOrderStatusEnum.PENDING) {
			order.setStatus(PaymentOrderStatusEnum.CLOSE);
			paymentOrderDao.updateById(order);
		} else {
			throw new ServerException(2005);
		}
	}

	public PaymentOrderDTO createOrder(PaymentOrderDTO request) {
		PaymentOrder order = modelMapper.map(request, PaymentOrder.class);
		order.setPlatform(getPlatform(order.getId(), order.getUserId()));
		paymentOrderDao.insert(order);
		return modelMapper.map(order, PaymentOrderDTO.class);
	}

	public void updateThirdInfo(PaymentUpdateThirdInfoRequest request) {
		PaymentOrder order = paymentOrderDao.selectById(request.getId());
		if (order != null) {
			if (!StringUtils.isBlank(request.getThirdOrderNo())) {
				order.setThirdOrderNo(request.getThirdOrderNo());
			}
			paymentOrderDao.updateById(order);
		}
	}

	private String getPlatform(Long id, Long userId) {
		String platform = (String) redisTemplate.opsForHash().get(RedisKey.OPTION_USER_PAYMENT_PLATFORM_KEY, userId);
		log.info("payment order {} get cache platform {}", id, platform);
		if (StringUtils.isBlank(platform) || (!platform.equals(PlatformEnum.H5.name())
				&& !platform.equals(PlatformEnum.IOS.name()) && !platform.equals(PlatformEnum.ANDROID.name()))) {
			platform = PlatformEnum.H5.name();
		}
		return platform;
	}

	public Integer inviteRechargePeopleBySymbol(String symbol) {
		return paymentOrderDao.inviteRechargePeopleBySymbol(symbol);
	}

	public List<WithdrawAmountDTO> totalRechargeAmountByUsers(List<Long> uidList) {
		return paymentOrderDao.totalRechargeAmountByUsers(uidList);
	}

}
