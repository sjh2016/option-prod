package com.waben.option.thirdparty.service;

import com.waben.option.common.component.SpringContext;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentApiConfigAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentPassagewayAPI;
import com.waben.option.common.interfaces.thirdparty.WithdrawOrderAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayCoinSuccessRequest;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PayOtcSuccessRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PaymentEntryService {

	@Resource
	private PaymentApiConfigAPI apiConfigAPI;

	@Resource
	private PaymentPassagewayAPI passagewayAPI;

	@Resource
	private PaymentOrderAPI paymentOrderAPI;

	@Resource
	private WithdrawOrderAPI withdrawOrderAPI;

	@Resource
	private UserAPI userAPI;

	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request) {
		PaymentPassagewayDTO passageway = passagewayAPI.query(request.getPassagewayId());
		PaymentApiConfigDTO payApiConfig = apiConfigAPI.query(passageway.getPayApiId());
		if (payApiConfig.getCashType() == PaymentCashType.PAYMENT_OTC) {
			BigDecimal reqMoney = request.getReqMoney().setScale(request.getReqCurrency().getPrecision(),
					RoundingMode.DOWN);
			if (reqMoney.compareTo(BigDecimal.ZERO) <= 0)
				throw new ServerException(1001);
			// 获取汇率
			BigDecimal exchangeRate = getExchangeRate(request, passageway);
			BigDecimal reqNum = request.getReqMoney().multiply(exchangeRate)
					.setScale(request.getReqCurrency().getPrecision(), RoundingMode.DOWN);
			// 检查限额
			if (passageway.getMinAmount() != null && reqNum.compareTo(passageway.getMinAmount()) < 0) {
				throw new ServerException(2011);
			}
			if (passageway.getMaxAmount() != null && reqNum.compareTo(passageway.getMaxAmount()) > 0) {
				throw new ServerException(2011);
			}
			request.setReqMoney(reqMoney);
			request.setReqNum(reqNum);
			request.setExchangeRate(exchangeRate);
		}
		// 检查通道是否可用
		PaymentApiConfigDTO.PaymentMethodDTO method = verifyChannelEnable(passageway, payApiConfig);
		// 检查用户
		UserDTO user = userAPI.queryUser(userId);
		// 判断支付信息
//        if (passageway.getNeedKyc() != null && passageway.getNeedKyc()) {
//            if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getMobilePhone()) || StringUtils.isBlank(user.getEmail())) {
//                throw new ServerException(2000);
//            }
//        }
		// 调用支付api对应的支付方法
		PaymentService payService = SpringContext.getBean(payApiConfig.getBeanName(), PaymentService.class);
		return payService.pay(userId, userIp, request, user, payApiConfig, method, passageway);
	}

	private BigDecimal getExchangeRate(PayFrontRequest request, PaymentPassagewayDTO passageway) {
		List<PaymentPassagewayDTO.ExchangeRateDTO> exchangeRateList = passageway.getExchangeRateList();
		if (exchangeRateList != null && exchangeRateList.size() > 0) {
			for (PaymentPassagewayDTO.ExchangeRateDTO rate : exchangeRateList) {
				if (rate.getCurrency().equals(request.getReqCurrency().name())) {
					return rate.getExchangeRate();
				}
			}
		}
		throw new ServerException(2010);
	}

	private PaymentApiConfigDTO.PaymentMethodDTO verifyChannelEnable(PaymentPassagewayDTO passageway,
			PaymentApiConfigDTO payApiConfig) {
		if (!payApiConfig.getEnable()) {
			throw new ServerException(2012);
		}
		List<PaymentApiConfigDTO.PaymentMethodDTO> methodList = payApiConfig.getMethodList();
		if (methodList == null || methodList.size() == 0) {
			throw new ServerException(2013);
		}
		PaymentApiConfigDTO.PaymentMethodDTO method = null;
		for (PaymentApiConfigDTO.PaymentMethodDTO dto : methodList) {
			if (dto.getId().equals(passageway.getPayMethodId())) {
				method = dto;
			}
		}
		if (method == null) {
			throw new ServerException(2014);
		}
		if (!method.getEnable()) {
			throw new ServerException(2015);
		}
		return method;
	}

	public String payCallback(boolean isThirdOrderNo, String orderNo, Long payApiId, Map<String, String> data) {
		// 查询支付api配置
		PaymentOrderDTO order = paymentOrderAPI.queryByOrderNo(orderNo);
		if (order == null)
			return "fail";
		PaymentApiConfigDTO config;
		if (payApiId != null) {
			config = apiConfigAPI.query(payApiId);
		} else {
			config = apiConfigAPI.query(order.getPayApiId());
		}
		// 获取支付api对应的service
		PaymentService payService = SpringContext.getBean(config.getBeanName(), PaymentService.class);
		PayCallbackHandleResult hr = payService.payCallback(config, data);
		if (hr.isPaySuccess()) {
			if (config.getCashType() == PaymentCashType.PAYMENT_OTC) {
				log.info("接收到OTC支付成功回调，开始处理 {}", orderNo);
				// OTC购买成功
				if (order.getStatus() == PaymentOrderStatusEnum.SUCCESS) {
					log.info("接收到OTC支付成功回调，但订单已处理过 {}", orderNo);
				} else if (order.getStatus() == PaymentOrderStatusEnum.CLOSE) {
					log.info("接收到OTC支付成功回调，但订单已关闭 {}", orderNo);
				} else if (order.getStatus() == PaymentOrderStatusEnum.PENDING) {
					log.info("接收到OTC支付成功回调，处理订单 {}", orderNo);
					PayOtcSuccessRequest req = new PayOtcSuccessRequest();
					req.setOrderNo(order.getOrderNo());
					req.setThirdOrderNo(hr.getThirdOrderNo());
					req.setRealMoney(hr.getRealMoney());
					paymentOrderAPI.payOtcSuccess(req);
				}
			} else if (config.getCashType() == PaymentCashType.PAYMENT_COIN) {
				log.info("接收到COIN支付成功回调，开始处理 {}", hr.getThirdOrderNo());
				// 充币成功
				String thirdOrderNo = hr.getThirdOrderNo();
				boolean hasOrder = paymentOrderAPI.hasThirdOrderNo(thirdOrderNo);
				if (hasOrder) {
					log.info("接收到COIN支付成功回调，但订单已存在 {}", thirdOrderNo);
				} else {
					log.info("接收到COIN支付成功回调，处理订单 {}", thirdOrderNo);
					PayCoinSuccessRequest req = new PayCoinSuccessRequest();
					req.setUserId(hr.getUserId());
					req.setThirdOrderNo(thirdOrderNo);
					req.setRealMoney(hr.getRealMoney());
					req.setReqCurrency(hr.getReqCurrency());
					req.setPayApiId(config.getId());
					req.setPayApiName(config.getName());
					req.setPayMethodId(config.getMethodList().get(0).getId());
					req.setPayMethodName(config.getMethodList().get(0).getName());
					req.setBurseAddress(hr.getBurseAddress());
					req.setBurseType(hr.getBurseType());
					req.setHash(hr.getHash());
					paymentOrderAPI.payCoinSuccess(req);
				}
			}
		}
		return hr.getBackThirdData();
	}

	public WithdrawSystemResult withdraw(WithdrawSystemRequest req) {
		PaymentPassagewayDTO passageway = passagewayAPI.query(req.getPassagewayId());
		PaymentApiConfigDTO payApiConfig = apiConfigAPI.query(passageway.getPayApiId());
		// 检查通道是否可用
		PaymentApiConfigDTO.PaymentMethodDTO method = verifyChannelEnable(passageway, payApiConfig);
		// 查询提现订单
		WithdrawOrderDTO order = withdrawOrderAPI.query(req.getId());
		// 检查用户
		UserDTO user = userAPI.queryUser(order.getUserId());
		if (payApiConfig.getNeedRealName() != null && payApiConfig.getNeedRealName()) {
			if (user.getName() == null || "".equals(user.getName().trim())) {
				throw new ServerException(2016);
			}
		}
		// 调用支付api对应的支付方法
		PaymentService payService = SpringContext.getBean(payApiConfig.getBeanName(), PaymentService.class);
		return payService.withdraw(req, user, order, payApiConfig, method);
	}

	public String withdrawCallback(String orderNo, Map<String, String> data) {
		// 查询支付api配置
		WithdrawOrderDTO order = withdrawOrderAPI.queryByOrderNo(orderNo);
		PaymentApiConfigDTO config = apiConfigAPI.query(order.getPayApiId());
		// 获取支付api对应的service
		PaymentService payService = SpringContext.getBean(config.getBeanName(), PaymentService.class);
		WithdrawCallbackHandleResult wr = payService.withdrawCallback(order, config, data);
		if (wr.getState() == 1) {
			// 成功
			withdrawOrderAPI.systemSuccessful(order.getId(), wr.getThirdOrderNo(), wr.getHash());
		} else if (wr.getState() == 2) {
			// 失败
			withdrawOrderAPI.systemFailed(order.getId());
		}
		return wr.getBackThirdData();
	}

}
