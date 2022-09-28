package com.waben.option.thirdparty.service.pay.shineupay;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.PayCallbackHandleResult;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.payment.WithdrawCallbackHandleResult;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.payment.WithdrawSystemResult;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.PaymentService;
import com.waben.option.thirdparty.service.ip.IpAddressService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class ShineupayPayService implements PaymentService {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private OkHttpClient okHttpClient;

	@Resource
	private PaymentOrderAPI paymentOrderAPI;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private IdWorker idWorker;

	@Resource
	private IpAddressService ipService;

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
		log.info("user: {}, shineupay payment request: {}", userId, request);
		// 创建订单
		Long orderId = idWorker.nextId();
		PaymentOrderDTO order = new PaymentOrderDTO();
		order.setId(orderId);
		order.setUserId(userId);
		order.setBrokerSymbol(user.getSymbol());
		order.setCashType(payApiConfig.getCashType());
		order.setOrderNo(String.valueOf(orderId));
		order.setStatus(PaymentOrderStatusEnum.PENDING);
		order.setReqNum(request.getReqNum());
		order.setReqMoney(request.getReqMoney());
		order.setRealMoney(BigDecimal.ZERO);
		order.setRealNum(BigDecimal.ZERO);
		order.setReqCurrency(request.getReqCurrency());
		order.setTargetCurrency(staticConfig.getDefaultCurrency());
		order.setFee(BigDecimal.ZERO);
		order.setPayApiId(payApiConfig.getId());
		order.setPayApiName(payApiConfig.getName());
		order.setPayMethodId(method.getId());
		order.setPayMethodName(method.getName());
		order.setPassagewayId(passageway.getId());
		order.setExchangeRate(request.getExchangeRate());
		order.setGmtCreate(LocalDateTime.now());
		paymentOrderAPI.createOrder(order);
		try {
			// 构建参数
			Map<String, Object> body = new TreeMap<>();
			body.put("orderId", String.valueOf(orderId));
			body.put("amount", request.getReqMoney().intValue());
			body.put("details", "recharge" + body.get("amount"));
			body.put("userId", String.valueOf(userId));
			body.put("notifyUrl", payApiConfig.getNotifyUrl());
			body.put("redirectUrl", payApiConfig.getReturnUrl());
			Map<String, Object> params = new TreeMap<>();
			params.put("merchantId", payApiConfig.getMerchantId());
			params.put("timestamp", System.currentTimeMillis());
			params.put("body", body);
			// 签名
			String paramString = JacksonUtil.encode(params);
			String sign = EncryptUtil.getMD5(paramString + "|" + payApiConfig.getSecretKey()).toLowerCase();
			// 请求上游
			log.info("shineupay payment param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).addHeader("Api-Sign", sign)
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("shineupay payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				int status = jsonNode.get("status").asInt();
				if (status == 0) {
					String payUrl = jsonNode.get("body").get("content").asText();
					String thirdOrderNo = jsonNode.get("body").get("transactionId").asText();
					// 更新上游单号
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(orderId);
					updateInfoReq.setThirdOrderNo(thirdOrderNo);
					paymentOrderAPI.updateThirdInfo(updateInfoReq);
					// 返回支付参数
					Map<String, Object> result = new HashMap<>();
					result.put("type", "4");
					result.put("content", payUrl);
					return result;
				} else {
					String msg = jsonNode.get("message").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL_WITH_MSG,
								new String[] { msg });
					} else {
						throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
					}
				}
			} else {
				throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
			}
		} catch (ServerException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("shineupay payment failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("shineupay payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String sign = data.get("sign");
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String checkSign = EncryptUtil.getMD5(body + "|" + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			int status = jsonNode.get("body").get("status").asInt();
			if (status == 1) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("body").get("amount").asText()));
			} else if (status == 91) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("body").get("paidAmount").asText()));
			} else {
				result.setPaySuccess(false);
			}
			result.setBackThirdData("success");
		} else {
			log.info("shineupay payment signature not match: {} - {}", sign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("fail");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method) {
		try {
			// 构建参数
			Map<String, Object> body = new TreeMap<>();
			body.put("version", "1.0.0");
			body.put("advPasswordMd5", EncryptUtil.getMD5(payApiConfig.getPrivateKey()).toLowerCase());
			body.put("orderId", order.getOrderNo());
			body.put("amount", request.getRealNum().intValue());
			body.put("receiveCurrency", order.getTargetCurrency().name());
			body.put("settlementCurrency", "");
			body.put("details", "withdraw" + body.get("amount"));
			body.put("notifyUrl", payApiConfig.getNotifyUrl());
			body.put("prodName", method.getParam());
			Map<String, Object> extInfo = new TreeMap<>();
			extInfo.put("userName", order.getName());
			extInfo.put("bankCode", order.getBankCode());
			extInfo.put("bankAccount", order.getBankCardId());
			extInfo.put("phone", user.getMobilePhone());
			extInfo.put("email", user.getEmail());
			body.put("extInfo", extInfo);
			Map<String, Object> params = new TreeMap<>();
			params.put("merchantId", payApiConfig.getMerchantId());
			params.put("timestamp", System.currentTimeMillis());
			params.put("body", body);
			// 签名
			String paramString = JacksonUtil.encode(params);
			String sign = EncryptUtil.getMD5(paramString + "|" + payApiConfig.getSecretKey()).toLowerCase();
			// 请求上游
			log.info("shineupay withdraw param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).header("Api-Sign", sign)
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("shineupay withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				int status = jsonNode.get("status").asInt();
				if (status == 0) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("body").get("platformOrderId").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("message").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL_WITH_MSG,
								"上游提示错误：" + msg);
					} else {
						throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
					}
				}
			} else {
				throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
			}
		} catch (ServerException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("shineupay withdraw failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("shineupay withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String sign = data.get("sign");
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String checkSign = EncryptUtil.getMD5(body + "|" + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			int status = jsonNode.get("body").get("status").asInt();
			if (status == 1) {
				result.setState(1);
			} else if (status == 2 || status == 4) {
				result.setState(2);
			} else {
				result.setState(3);
			}
			result.setBackThirdData("success");
		} else {
			log.info("shineupay withdraw signature not match: {} - {}", sign, checkSign);
			result.setBackThirdData("fail");
		}
		return result;
	}

}
