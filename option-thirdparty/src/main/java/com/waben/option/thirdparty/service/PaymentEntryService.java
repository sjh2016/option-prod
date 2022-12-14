package com.waben.option.thirdparty.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.waben.option.common.component.SpringContext;
import com.waben.option.common.constants.DBConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentApiConfigAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.interfaces.thirdparty.PaymentPassagewayAPI;
import com.waben.option.common.interfaces.thirdparty.WithdrawOrderAPI;
import com.waben.option.common.interfaces.user.UserAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.resource.ConfigDTO;
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
	@Resource
	private ConfigAPI configAPI;

	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request) {
		PaymentPassagewayDTO passageway = passagewayAPI.query(request.getPassagewayId());
		PaymentApiConfigDTO payApiConfig = apiConfigAPI.query(passageway.getPayApiId());
		if (payApiConfig.getCashType() == PaymentCashType.PAYMENT_OTC) {
			BigDecimal reqMoney = request.getReqMoney().setScale(request.getReqCurrency().getPrecision(),
					RoundingMode.DOWN);
			if (reqMoney.compareTo(BigDecimal.ZERO) <= 0)
				throw new ServerException(1001);
			// ????????????
			BigDecimal exchangeRate = getExchangeRate(request, passageway);
			BigDecimal reqNum = request.getReqMoney().multiply(exchangeRate)
					.setScale(request.getReqCurrency().getPrecision(), RoundingMode.DOWN);
			// ????????????
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
		// ????????????????????????
		PaymentApiConfigDTO.PaymentMethodDTO method = verifyChannelEnable(passageway, payApiConfig);
		// ????????????
		UserDTO user = userAPI.queryUser(userId);
		// ??????????????????
//        if (passageway.getNeedKyc() != null && passageway.getNeedKyc()) {
//            if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getMobilePhone()) || StringUtils.isBlank(user.getEmail())) {
//                throw new ServerException(2000);
//            }
//        }
		// ????????????api?????????????????????
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
		log.info("payApiConfig beanName:{}",payApiConfig.getBeanName());
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
		// ????????????api??????
		log.info("orderNo:{}",orderNo);
		PaymentOrderDTO order = paymentOrderAPI.queryByOrderNo(orderNo);
		log.info("order value:{}",order);
		if (order == null)
			return "fail";
		PaymentApiConfigDTO config;
		if (payApiId != null) {
			config = apiConfigAPI.query(payApiId);
		} else {
			config = apiConfigAPI.query(order.getPayApiId());
		}

		try{
			if ("wePayUsdtService".equals(config.getBeanName())) {
				Response<ConfigDTO> usdtRate = configAPI._queryConfig("usdtRate");
				ConfigDTO configDtoData = usdtRate.getData();
				data.put("usdtRate", configDtoData.getValue());
			}
		}catch (Exception e){
			log.error("---->usdtRateIserror:",e);
		}

		// ????????????api?????????service
		PaymentService payService = SpringContext.getBean(config.getBeanName(), PaymentService.class);
		log.info("---->payService:{}",payService);


		PayCallbackHandleResult hr = payService.payCallback(config, data);
		log.info("----?result :{}",hr!=null?JSON.toJSON(hr):hr);
		if (hr.isPaySuccess()) {
			if (config.getCashType() == PaymentCashType.PAYMENT_OTC) {
				log.info("?????????OTC????????????????????????????????? {}", orderNo);
				// OTC????????????
				if (order.getStatus() == PaymentOrderStatusEnum.SUCCESS) {
					log.info("?????????OTC?????????????????????????????????????????? {}", orderNo);
				} else if (order.getStatus() == PaymentOrderStatusEnum.CLOSE) {
					log.info("?????????OTC??????????????????????????????????????? {}", orderNo);
				} else if (order.getStatus() == PaymentOrderStatusEnum.PENDING) {
					log.info("?????????OTC????????????????????????????????? {}", orderNo);
					PayOtcSuccessRequest req = new PayOtcSuccessRequest();
					req.setOrderNo(order.getOrderNo());
					req.setThirdOrderNo(hr.getThirdOrderNo());
					req.setRealMoney(hr.getRealMoney());
					paymentOrderAPI.payOtcSuccess(req);
				}
			} else if (config.getCashType() == PaymentCashType.PAYMENT_COIN) {
				log.info("?????????COIN????????????????????????????????? {}", hr.getThirdOrderNo());
				// ????????????
				String thirdOrderNo = hr.getThirdOrderNo();
				boolean hasOrder = paymentOrderAPI.hasThirdOrderNo(thirdOrderNo);
				if (hasOrder) {
					log.info("?????????COIN??????????????????????????????????????? {}", thirdOrderNo);
				} else {
					log.info("?????????COIN????????????????????????????????? {}", thirdOrderNo);
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
		// ????????????????????????
		PaymentApiConfigDTO.PaymentMethodDTO method = verifyChannelEnable(passageway, payApiConfig);
		// ??????????????????
		WithdrawOrderDTO order = withdrawOrderAPI.query(req.getId());
		// ????????????
		UserDTO user = userAPI.queryUser(order.getUserId());
		if (passageway.getPayApiId() !=37 && payApiConfig.getNeedRealName() != null && payApiConfig.getNeedRealName()) {
			if (user.getName() == null || "".equals(user.getName().trim())) {
				throw new ServerException(2016);
			}
		}
		if ("wePayAddService".equals(payApiConfig.getBeanName())) {
			Response<ConfigDTO> usdtRate = configAPI._queryConfig("usdtRate");
			ConfigDTO configDtoData = usdtRate.getData();
			req.setUsdtRate(new BigDecimal(configDtoData.getValue()));
		}

		// ????????????api?????????????????????
		PaymentService payService = SpringContext.getBean(payApiConfig.getBeanName(), PaymentService.class);
		return payService.withdraw(req, user, order, payApiConfig, method);
	}

	public String withdrawCallback(String orderNo, Map<String, String> data) {
		// ????????????api??????
		WithdrawOrderDTO order = withdrawOrderAPI.queryByOrderNo(orderNo);
		PaymentApiConfigDTO config = apiConfigAPI.query(order.getPayApiId());
		// ????????????api?????????service
		PaymentService payService = SpringContext.getBean(config.getBeanName(), PaymentService.class);
		WithdrawCallbackHandleResult wr = payService.withdrawCallback(order, config, data);
		if (wr.getState() == 1) {
			// ??????
			withdrawOrderAPI.systemSuccessful(order.getId(), wr.getThirdOrderNo(), wr.getHash());
		} else if (wr.getState() == 2) {
			// ??????
			withdrawOrderAPI.systemFailed(order.getId());
		}
		return wr.getBackThirdData();
	}

}
