package com.waben.option.core.service.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.payment.WithdrawAdminStaDTO;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.payment.WithdrawSystemResult;
import com.waben.option.common.model.dto.resource.WithdrawConfigDTO;
import com.waben.option.common.model.dto.summary.WithdrawAmountDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import com.waben.option.common.model.enums.WithdrawTypeEnum;
import com.waben.option.common.model.request.payment.WithdrawAdminPageRequest;
import com.waben.option.common.model.request.payment.WithdrawOtcFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemProcessRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.model.request.payment.WithdrawUnderlineNotpassRequest;
import com.waben.option.common.model.request.payment.WithdrawUnderlineSuccessfulRequest;
import com.waben.option.common.model.request.payment.WithdrawUserPageRequest;
import com.waben.option.common.util.TimeUtil;
import com.waben.option.core.service.account.AccountService;
import com.waben.option.core.service.resource.ConfigService;
import com.waben.option.data.entity.payment.BindCard;
import com.waben.option.data.entity.payment.WithdrawOrder;
import com.waben.option.data.entity.resource.Level;
import com.waben.option.data.entity.user.User;
import com.waben.option.data.entity.user.UserSta;
import com.waben.option.data.repository.payment.BindCardDao;
import com.waben.option.data.repository.payment.WithdrawOrderDao;
import com.waben.option.data.repository.resource.LevelDao;
import com.waben.option.data.repository.user.UserDao;
import com.waben.option.data.repository.user.UserStaDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WithdrawOrderService {

	@Resource
	private WithdrawOrderDao withdrawOrderDao;

	@Resource
	private UserDao userDao;

	@Resource
	private UserStaDao userStaDao;

	@Resource
	private LevelDao levelDao;

	@Resource
	private BindCardDao bindCardDao;

	@Resource
	private AccountService accountService;

	@Resource
	private PaymentPassagewayService passagewayService;

	@Resource
	private PaymentApiConfigService apiConfigService;

	@Resource
	private ConfigService configService;

	@Resource
	private PaymentAPI paymentAPI;

	@Resource
	public PasswordEncoder passwordEncoder;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private IdWorker idWorker;

	@Resource
	private ModelMapper modelMapper;

	public WithdrawOrderDTO query(Long id) {
		WithdrawOrder order = withdrawOrderDao.selectById(id);
		if (order == null) {
			throw new ServerException(2017);
		} else {
			return modelMapper.map(order, WithdrawOrderDTO.class);
		}
	}

	public WithdrawOrderDTO queryByOrderNo(String orderNo) {
		QueryWrapper<WithdrawOrder> query = new QueryWrapper<>();
		query.eq(WithdrawOrder.ORDER_NO, orderNo);
		WithdrawOrder order = withdrawOrderDao.selectOne(query);
		if (order == null) {
			throw new ServerException(2017);
		} else {
			return modelMapper.map(order, WithdrawOrderDTO.class);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void placeOtcOrder(Long userId, WithdrawOtcFrontRequest req) {
		log.info("user {} request withdraw otc: {}", userId, req);
		if (req.getReqNum() == null || req.getReqNum().compareTo(BigDecimal.ZERO) < 0
				|| req.getTargetCurrency() == null) {
			throw new ServerException(1001);
		}
		// 检查用户
		User user = userDao.selectById(userId);
		if (user == null)
			throw new ServerException(1001);
		/*
		 * // 验证支付密码 if (req.getFundPassword() == null || user.getFundPassword() == null
		 * || !passwordEncoder.matches(req.getFundPassword(), user.getFundPassword()))
		 * throw new ServerException(2018);
		 */
		// 检查通道
		PaymentPassagewayDTO passageway = passagewayService.query(req.getPassagewayId());
		PaymentApiConfigDTO apiConfig = apiConfigService.query(passageway.getPayApiId());
		if (passageway.getNeedKyc() != null && passageway.getNeedKyc()) {
			if (StringUtils.isBlank(user.getName())) {
				throw new ServerException(2000);
			}
		}
		// 判断限额
		boolean isVip = (user.getIsVip() != null && user.getIsVip()) ? true : false;
		if (isVip) {
			if (passageway.getVipMinAmount() != null && req.getReqNum().compareTo(passageway.getVipMinAmount()) < 0) {
				throw new ServerException(2020);
			}
		} else {
			if (passageway.getMinAmount() != null && req.getReqNum().compareTo(passageway.getMinAmount()) < 0) {
				throw new ServerException(2020);
			}
		}
		if (passageway.getMaxAmount() != null && req.getReqNum().compareTo(passageway.getMaxAmount()) > 0) {
			throw new ServerException(2020);
		}
		// 手续费
		BigDecimal feeRate = BigDecimal.ZERO;
		BigDecimal taxRate = BigDecimal.ZERO;
		BigDecimal exchangeRate = getExchangeRate(req.getTargetCurrency().name(), passageway);
		if ("bank_card".equals(passageway.getLogo())) {
			feeRate = getFeeRate(req.getTargetCurrency().name(), passageway, isVip);
			taxRate = getTaxRate(req.getTargetCurrency().name(), passageway, isVip);
		} else {
			// 等级限制
			Level level = levelDao.selectOne(new QueryWrapper<Level>().eq(Level.LEVEL, user.getLevel()));
			if (level == null)
				throw new ServerException(1001);
			if (req.getReqNum().compareTo(level.getLimitAmount()) < 0)
				throw new ServerException(2020);
			feeRate = level.getFee();
		}
		BigDecimal reqMoney = req.getReqNum().divide(exchangeRate).setScale(req.getTargetCurrency().getPrecision(),
				RoundingMode.DOWN);
		BigDecimal fee = req.getReqNum().multiply(feeRate.add(taxRate)).setScale(0, RoundingMode.DOWN);
		log.info("fee:{},feeRate:{},taxRate:{}",fee,feeRate,taxRate);
		log.info("fee value:{}",fee.compareTo(req.getReqNum()));
		if (fee.compareTo(req.getReqNum()) >= 0) {
			throw new ServerException(2024);
		}
		BigDecimal realNum = req.getReqNum().subtract(fee).divide(exchangeRate)
				.setScale(req.getTargetCurrency().getPrecision(), RoundingMode.DOWN);
		// 判断余额是否足够
		List<UserAccountDTO> account = accountService.queryAccountList(Lists.newArrayList(userId),
				staticConfig.getDefaultCurrency());
		if (account == null || account.size() == 0 || account.get(0).getBalance()
				.subtract(account.get(0).getFreezeCapital()).compareTo(req.getReqNum()) < 0) {
			throw new ServerException(2019);
		}
		// 判断是否在允许提现时间内
		checkIsWithdrawTime(req.getReqNum(), false);
		// 创建提现订单
		WithdrawOrder order = new WithdrawOrder();
		Long id = idWorker.nextId();
		order.setId(id);
		order.setUserId(userId);
		order.setBrokerSymbol(user.getSymbol());
		order.setCashType(apiConfig.getCashType());
		order.setOrderNo(String.valueOf(id));
		order.setStatus(WithdrawOrderStatusEnum.PENDING);
		order.setReqNum(req.getReqNum());
		order.setReqMoney(reqMoney);
		order.setRealNum(realNum);
		order.setReqCurrency(staticConfig.getDefaultCurrency());
		order.setTargetCurrency(req.getTargetCurrency());
		order.setExchangeRate(exchangeRate);
		order.setFee(fee);
		// 检查绑卡
		if (StringUtils.isEmpty(req.getBurseAddress())) {
			BindCard bindCard = bindCardDao.selectById(req.getBindId());
			if (bindCard == null)
				throw new ServerException(1001);
			order.setName(bindCard.getName());
			order.setBranchName(bindCard.getBranchName());
			order.setBankName(bindCard.getBankName().trim());
			order.setBankCardId(bindCard.getBankCardId().trim());
			if (bindCard.getMobilePhone() != null) {
				order.setMobilePhone(bindCard.getMobilePhone().trim());
			}
			// 获取bankCode
			String[] supportIdArr = bindCard.getSupportUpId().split(",");
			int supportIndex = -1;
			for (int i = 0; i < supportIdArr.length; i++) {
				String supportId = supportIdArr[i];
				if (supportId.equals(passageway.getPayApiId().toString())) {
					supportIndex = i;
					break;
				}
			}
			if (supportIndex >= 0) {
				String[] supportCodeArr = bindCard.getSupportUpCode().split(",");
				order.setBankCode(supportCodeArr[supportIndex].trim());
			} else {
				throw new ServerException(2060);
			}
		} else {
			order.setBurseAddress(req.getBurseAddress().trim());
			order.setBurseType(order.getTargetCurrency().name());
		}
		order.setPayApiId(passageway.getPayApiId());
		order.setPayApiName(apiConfig.getName());
		order.setPayMethodId(passageway.getPayMethodId());
		PaymentApiConfigDTO.PaymentMethodDTO method = verifyPaymentMethod(passageway, apiConfig);
		order.setPayMethodName(method.getName());
		order.setPassagewayId(passageway.getId());
		order.setIsLuckyProfit(false);
		withdrawOrderDao.insert(order);
		// 冻结提现金额
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
				.type(TransactionEnum.FREEZE_WITHDRAW_OTC).amount(order.getReqNum()).transactionId(order.getId())
				.currency(order.getReqCurrency()).build());
		accountService.transaction(order.getUserId(), transactionBeanList);
	}

	private BigDecimal getFeeRate(String currency, PaymentPassagewayDTO passageway, boolean isVip) {
		List<PaymentPassagewayDTO.ExchangeRateDTO> exchangeRateList = passageway.getExchangeRateList();
		if (exchangeRateList != null && exchangeRateList.size() > 0) {
			for (PaymentPassagewayDTO.ExchangeRateDTO rate : exchangeRateList) {
				if (rate.getCurrency().equals(currency)) {
					BigDecimal result = null;
					if (isVip) {
						result = rate.getVipFeeRate();
					} else {
						result = rate.getFeeRate();
					}
					if (result == null) {
						result = BigDecimal.ZERO;
					}
					return result;
				}
			}
		}
		throw new ServerException(2010);
	}

	private BigDecimal getTaxRate(String currency, PaymentPassagewayDTO passageway, boolean isVip) {
		List<PaymentPassagewayDTO.ExchangeRateDTO> exchangeRateList = passageway.getExchangeRateList();
		if (exchangeRateList != null && exchangeRateList.size() > 0) {
			for (PaymentPassagewayDTO.ExchangeRateDTO rate : exchangeRateList) {
				if (rate.getCurrency().equals(currency)) {
					BigDecimal result = null;
					if (isVip) {
						result = rate.getVipTaxRate();
					} else {
						result = rate.getTaxRate();
					}
					if (result == null) {
						result = BigDecimal.ZERO;
					}
					return result;
				}
			}
		}
		throw new ServerException(2010);
	}

	private BigDecimal getExchangeRate(String currency, PaymentPassagewayDTO passageway) {
		List<PaymentPassagewayDTO.ExchangeRateDTO> exchangeRateList = passageway.getExchangeRateList();
		if (exchangeRateList != null && exchangeRateList.size() > 0) {
			for (PaymentPassagewayDTO.ExchangeRateDTO rate : exchangeRateList) {
				if (rate.getCurrency().equals(currency)) {
					BigDecimal result = rate.getExchangeRate();
					if (result != null) {
						return result;
					}
				}
			}
		}
		throw new ServerException(2010);
	}

	private PaymentApiConfigDTO.PaymentMethodDTO verifyPaymentMethod(PaymentPassagewayDTO passageway,
			PaymentApiConfigDTO apiConfig) {
		List<PaymentApiConfigDTO.PaymentMethodDTO> methodList = apiConfig.getMethodList();
		if (methodList == null || methodList.size() == 0) {
			throw new ServerException(2013);
		}
		for (PaymentApiConfigDTO.PaymentMethodDTO dto : methodList) {
			if (dto.getId().equals(passageway.getPayMethodId())) {
				return dto;
			}
		}
		throw new ServerException(2014);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void underlineSuccessful(Long auditUserId, WithdrawUnderlineSuccessfulRequest req) {
		WithdrawOrder order = withdrawOrderDao.selectById(req.getId());
		if (order == null) {
			throw new ServerException(1001);
		}
		if (order.getStatus() != WithdrawOrderStatusEnum.PENDING) {
			throw new ServerException(2021);
		}
		order.setStatus(WithdrawOrderStatusEnum.SUCCESSFUL);
		order.setType(WithdrawTypeEnum.UNDERLINE);
		order.setRemark(req.getRemark());
		order.setArrivalTime(LocalDateTime.now());
		order.setAuditTime(LocalDateTime.now());
		order.setAuditUserId(auditUserId);
		withdrawOrderDao.updateById(order);
		// 解冻并扣除提现金额
		accountDoWithdrawSuccessful(order);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void underlineNotpass(Long auditUserId, WithdrawUnderlineNotpassRequest req) {
		WithdrawOrder order = withdrawOrderDao.selectById(req.getId());
		if (order == null) {
			throw new ServerException(1001);
		}
		if (order.getStatus() != WithdrawOrderStatusEnum.PENDING) {
			throw new ServerException(2021);
		}
		order.setStatus(WithdrawOrderStatusEnum.NOTPASS);
		order.setType(WithdrawTypeEnum.UNDERLINE);
		order.setRemark(req.getRemark());
		order.setAuditTime(LocalDateTime.now());
		order.setAuditUserId(auditUserId);
		withdrawOrderDao.updateById(order);
		// 解冻提现金额
		accountDoWithdrawFailed(order);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void systemProcess(Long auditUserId, WithdrawSystemProcessRequest req) {
		log.info("withdraw order {} do system withdraw by {}, realNum {}, remark {}", req.getId(), auditUserId,
				req.getRealNum(), req.getRemark());
		WithdrawOrder order = withdrawOrderDao.selectById(req.getId());
		if (order == null) {
			throw new ServerException(1001);
		}
		if (order.getStatus() != WithdrawOrderStatusEnum.PENDING) {
			throw new ServerException(2021);
		}
		if (order.getPassagewayId() != null) {
			req.setPassagewayId(order.getPassagewayId());
		}
		// 校验用户账号是否穿仓
		List<UserAccountDTO> account = accountService.queryAccountList(Lists.newArrayList(order.getUserId()),
				order.getReqCurrency());
		if (account != null && account.get(0).getBalance().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ServerException(2019);
		}
		// 检查修改实际到账金额参数
		BigDecimal realNum = req.getRealNum();
		PaymentPassagewayDTO passageway = passagewayService.query(req.getPassagewayId());
		PaymentApiConfigDTO apiConfig = apiConfigService.query(passageway.getPayApiId());
		// 请求系统提现
		if (realNum != null) {
			order.setRealNum(realNum);
		} else {
			realNum = order.getRealNum();
		}
		WithdrawSystemRequest withdrawReq = new WithdrawSystemRequest();
		withdrawReq.setPassagewayId(req.getPassagewayId());
		withdrawReq.setId(req.getId());
		withdrawReq.setRealNum(realNum.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN));
		WithdrawSystemResult withdrawResult = paymentAPI.withdraw(withdrawReq);
		// 修改订单
		order.setPassagewayId(req.getPassagewayId());
		order.setPayApiId(passageway.getPayApiId());
		order.setPayApiName(apiConfig.getName());
		order.setPayMethodId(passageway.getPayMethodId());
		order.setStatus(WithdrawOrderStatusEnum.PROCESSING);
		order.setType(WithdrawTypeEnum.SYSTEM);
		order.setRemark(req.getRemark());
		order.setAuditTime(LocalDateTime.now());
		order.setAuditUserId(auditUserId);
		if (withdrawResult.getThirdOrderNo() != null) {
			order.setThirdOrderNo(withdrawResult.getThirdOrderNo());
		}
		if (withdrawResult.getThirdRespMsg() != null) {
			order.setThirdRespMsg(withdrawResult.getThirdRespMsg());
		}
		if (withdrawResult.isImmediateSuccess()) {
			order.setStatus(WithdrawOrderStatusEnum.SUCCESSFUL);
			order.setArrivalTime(LocalDateTime.now());
			accountDoWithdrawSuccessful(order);
		}
		withdrawOrderDao.updateById(order);
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void systemSuccessful(Long id, String thirdOrderNo, String hash) {
		log.info("withdraw order {} system successful, thirdOrderNo {}, hash {}", id, thirdOrderNo, hash);
		synchronized (String.valueOf(id).intern()) {
			WithdrawOrder order = withdrawOrderDao.selectById(id);
			if (order.getStatus() == WithdrawOrderStatusEnum.SUCCESSFUL) {
				log.info("withdraw order {} system successful, but order already handle", id);
				if (thirdOrderNo != null) {
					order.setThirdOrderNo(thirdOrderNo);
				}
				if (hash != null) {
					order.setHash(hash);
				}
				withdrawOrderDao.updateById(order);
			} else if (order.getStatus() == WithdrawOrderStatusEnum.PROCESSING) {
				// 修改订单状态
				order.setStatus(WithdrawOrderStatusEnum.SUCCESSFUL);
				if (thirdOrderNo != null) {
					order.setThirdOrderNo(thirdOrderNo);
				}
				if (hash != null) {
					order.setHash(hash);
				}
				order.setArrivalTime(LocalDateTime.now());
				withdrawOrderDao.updateById(order);
				// 解冻并扣除提现金额
				accountDoWithdrawSuccessful(order);
			} else {
				log.info("withdraw order {} system successful, but order status not match, status {}", id,
						order.getStatus());
				throw new ServerException(2022);
			}
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	public void systemFailed(Long id) {
		log.info("withdraw order {} system failed", id);
		synchronized (String.valueOf(id).intern()) {
			WithdrawOrder order = withdrawOrderDao.selectById(id);
			if (order.getStatus() == WithdrawOrderStatusEnum.FAILED) {
				log.info("withdraw order {} system failed, but order already handle", id);
				return;
			} else if (order.getStatus() == WithdrawOrderStatusEnum.PROCESSING) {
				// 修改订单状态
				order.setStatus(WithdrawOrderStatusEnum.FAILED);
				withdrawOrderDao.updateById(order);
				// 解冻提现金额
				accountDoWithdrawFailed(order);
			} else {
				log.info("withdraw order {} system failed, but order status not match, status {}", id,
						order.getStatus());
				throw new ServerException(2022);
			}
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	void accountDoWithdrawSuccessful(WithdrawOrder order) {
		// 增加用户总提现金额
		UserSta userSta = userStaDao.selectById(order.getUserId());
		BigDecimal totalWithdraw = userSta.getTotalWithdrawAmount();
		if (totalWithdraw == null) {
			totalWithdraw = BigDecimal.ZERO;
		}
		userSta.setTotalWithdrawAmount(totalWithdraw.add(order.getRealNum()));
		userStaDao.updateById(userSta);
		// 解冻并扣除提现金额
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		if (order.getCashType() == PaymentCashType.WITHDRAW_OTC) {
			transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
					.type(TransactionEnum.UNFREEZE_WITHDRAW_OTC).amount(order.getReqNum()).transactionId(order.getId())
					.currency(order.getReqCurrency()).build());
			transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
					.type(TransactionEnum.DEBIT_WITHDRAW_OTC).amount(order.getReqNum()).transactionId(order.getId())
					.currency(order.getReqCurrency()).build());
		}
		if (transactionBeanList.size() > 0) {
			accountService.transaction(order.getUserId(), transactionBeanList);
		}
	}

//    @ShardingTransactionType(value = TransactionType.XA)
	@Transactional(rollbackFor = Exception.class)
	void accountDoWithdrawFailed(WithdrawOrder order) {
		// 解冻提现金额
		List<AccountTransactionBean> transactionBeanList = new ArrayList<>();
		if (order.getCashType() == PaymentCashType.WITHDRAW_OTC) {
			transactionBeanList.add(AccountTransactionBean.builder().userId(order.getUserId())
					.type(TransactionEnum.UNFREEZE_RETURN_WITHDRAW_OTC).amount(order.getReqNum()).transactionId(order.getId())
					.currency(order.getReqCurrency()).build());
		}
		if (transactionBeanList.size() > 0) {
			accountService.transaction(order.getUserId(), transactionBeanList);
		}
	}

	public PageInfo<WithdrawOrderDTO> userPage(Long userId, WithdrawUserPageRequest req) {
		Page<WithdrawOrder> page = new Page<>(req.getPage(), req.getSize());
		QueryWrapper<WithdrawOrder> query = new QueryWrapper<>();
		query.eq(WithdrawOrder.USER_ID, userId);
		if (req.getStart() != null && req.getStart() > 0) {
			query.ge(WithdrawOrder.GMT_CREATE, TimeUtil.getDateTime(req.getStart()));
		}
		if (req.getEnd() != null && req.getEnd() > 0) {
			query.lt(WithdrawOrder.GMT_CREATE, TimeUtil.getDateTime(req.getEnd()));
		}
		if (req.getCashType() != null) {
			query.eq(WithdrawOrder.CASH_TYPE, req.getCashType());
		}
		if (req.getStatusList() != null && req.getStatusList().size() > 0) {
			query.in(WithdrawOrder.STATUS, req.getStatusList());
		}
		query.orderByDesc(WithdrawOrder.GMT_CREATE);
		IPage<WithdrawOrder> pageData = withdrawOrderDao.selectPage(page, query);
		return new PageInfo<>(pageData.getRecords().stream().map(temp -> modelMapper.map(temp, WithdrawOrderDTO.class))
				.collect(Collectors.toList()), pageData.getTotal(), req.getPage(), req.getSize());
	}

	public PageInfo<WithdrawOrderDTO> adminPage(WithdrawAdminPageRequest req) {
		if (req.getStatus() != null && req.getStatus() == WithdrawOrderStatusEnum.PENDING) {
			req.setIsBlack(false);
		} else if (req.getStatus() != null && req.getStatus() == WithdrawOrderStatusEnum.PENDING_SHUAZI) {
			req.setStatus(WithdrawOrderStatusEnum.PENDING);
			req.setIsBlack(true);
		}
		req.setLimit((req.getPage() - 1) * req.getSize());
		if (staticConfig.isContract()) {
			List<WithdrawOrderDTO> list = withdrawOrderDao.adminPage(req);
			Integer total = withdrawOrderDao.adminCount(req);
			return new PageInfo<>(list, total != null ? total : 0, req.getPage(), req.getSize());
		} else {
			List<WithdrawOrderDTO> list = withdrawOrderDao.adminRealPage(req);
			Integer total = withdrawOrderDao.adminCount(req);
			return new PageInfo<>(list, total != null ? total : 0, req.getPage(), req.getSize());
		}
	}

	@Deprecated
	public PageInfo<WithdrawOrderDTO> adminPage_old(WithdrawAdminPageRequest req) {
		Page<WithdrawOrder> page = new Page<>(req.getPage(), req.getSize());
		QueryWrapper<WithdrawOrder> query = new QueryWrapper<>();
		if (req.getCashType() != null) {
			query.eq(WithdrawOrder.CASH_TYPE, req.getCashType());
		}
		if (!StringUtils.isBlank(req.getOrderNo())) {
			query.eq(WithdrawOrder.ORDER_NO, req.getOrderNo().trim());
		}
		if (!StringUtils.isBlank(req.getThirdOrderNo())) {
			query.eq(WithdrawOrder.THIRD_ORDER_NO, req.getThirdOrderNo().trim());
		}
		if (req.getPayApiId() != null) {
			query.eq(WithdrawOrder.PAY_API_ID, req.getPayApiId());
		}
		if (req.getStatus() != null) {
			query.eq(WithdrawOrder.STATUS, req.getStatus());
		}
		if (req.getUidList() != null && req.getUidList().size() > 0) {
			query.in(WithdrawOrder.USER_ID, req.getUidList());
		}
		if (req.getBrokerSymbolList() != null && req.getBrokerSymbolList().size() > 0) {
			query.in(WithdrawOrder.BROKER_SYMBOL, req.getBrokerSymbolList());
		}
		if (req.getStartTime() != null) {
			query.ge(WithdrawOrder.GMT_CREATE, req.getStartTime());
		}
		if (req.getEndTime() != null) {
			query.lt(WithdrawOrder.GMT_CREATE, req.getEndTime());
		}
		if (req.getArrivalStart() != null) {
			query.ge(WithdrawOrder.ARRIVAL_TIME, req.getArrivalStart());
		}
		if (req.getArrivalEnd() != null) {
			query.lt(WithdrawOrder.ARRIVAL_TIME, req.getArrivalEnd());
		}
		if (req.getMobilePhone() != null) {
			query.eq(WithdrawOrder.MOBILE_PHONE, req.getMobilePhone());
		}
		query.orderByDesc(WithdrawOrder.GMT_CREATE);
		IPage<WithdrawOrder> pageData = withdrawOrderDao.selectPage(page, query);
		return new PageInfo<>(pageData.getRecords().stream().map(temp -> modelMapper.map(temp, WithdrawOrderDTO.class))
				.collect(Collectors.toList()), pageData.getTotal(), req.getPage(), req.getSize());
	}

	public WithdrawAdminStaDTO adminSta(WithdrawAdminPageRequest req) {
		WithdrawAdminStaDTO result = withdrawOrderDao.adminSta(req);
		if (result.getReqNumTotal() == null) {
			result.setReqNumTotal(BigDecimal.ZERO);
		}
		if (result.getRealNumTotal() == null) {
			result.setRealNumTotal(BigDecimal.ZERO);
		}
		return result;
	}

	private void checkIsWithdrawTime(BigDecimal reqNum, boolean isCoin) {
		List<WithdrawConfigDTO> configList = configService.queryWithdrawConfig();
		if (configList != null && configList.size() > 0) {
			WithdrawConfigDTO config = configList.get(0);
			LocalDateTime dateTime = LocalDateTime.now();
			LocalTime localTime = dateTime.toLocalTime();
			int weekday = dateTime.getDayOfWeek().getValue();
			if (localTime.isAfter(config.getEndTime()) || localTime.isBefore(config.getStartTime())) {
				throw new ServerException(2023);
			}
			if (!config.getWeekList().contains(weekday)) {
				throw new ServerException(2024);
			}
			if (isCoin && config.getMinAmount() != null && reqNum.compareTo(config.getMinAmount()) < 0) {
				throw new ServerException(2020);
			}
		}
	}

	public boolean isWithdrawTime() {
		List<WithdrawConfigDTO> configList = configService.queryWithdrawConfig();
		if (configList != null && configList.size() > 0) {
			WithdrawConfigDTO config = configList.get(0);
			LocalDateTime dateTime = LocalDateTime.now();
			LocalTime localTime = dateTime.toLocalTime();
			int weekday = dateTime.getDayOfWeek().getValue();
			if (!config.getWeekList().contains(weekday)) {
				return false;
			}
			if (localTime.isAfter(config.getEndTime()) || localTime.isBefore(config.getStartTime())) {
				return false;
			}
		}
		return true;
	}

	public List<WithdrawAmountDTO> totalWithdrawAmountByUsers(List<Long> uidList) {
		return withdrawOrderDao.totalWithdrawAmountByUsers(uidList);
	}

}
