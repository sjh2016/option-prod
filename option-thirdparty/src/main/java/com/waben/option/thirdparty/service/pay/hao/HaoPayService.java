package com.waben.option.thirdparty.service.pay.hao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.PayCallbackHandleResult;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.payment.WithdrawCallbackHandleResult;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.payment.WithdrawSystemResult;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.pay.AbstractPaymentService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class HaoPayService extends AbstractPaymentService {

	@Resource
	private OkHttpClient okHttpClient;

	@Resource
	private PaymentOrderAPI paymentOrderAPI;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private ObjectMapper objectMapper;

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method,
			PaymentPassagewayDTO passageway) {
		log.info("user: {}, haoz payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// ????????????
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("appid", payApiConfig.getMerchantId());
			params.put("mer_order_no", String.valueOf(orderId));
			params.put("currency", staticConfig.getDefaultCurrency().toString());
			params.put("amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("returnUrl", payApiConfig.getReturnUrl());
			// ??????
			String sign = EncryptUtil.getMD5(orderId.toString() + params.get("amount") + payApiConfig.getSecretKey())
					.toLowerCase();
			params.put("sign", sign);
			// ????????????
			String paramData = objectMapper.writeValueAsString(params);
			log.info("haoz payment param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("haoz payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("status").asText();
				if ("success".equals(code)) {
					Long orderNo = jsonNode.get("mer_order_no").asLong();
					String thirdOrderNo = jsonNode.get("order_no").asText();
					String payUrl = jsonNode.get("url").asText();
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(orderNo);
					updateInfoReq.setThirdOrderNo(thirdOrderNo);
					paymentOrderAPI.updateThirdInfo(updateInfoReq);
					// ??????????????????
					Map<String, Object> result = new HashMap<>();
					result.put("type", "1");
					result.put("content", payUrl);
					return result;
				} else {
					String msg = jsonNode.get("msg").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(2001, msg);
					} else {
						throw new ServerException(2001);
					}
				}
			} else {
				throw new ServerException(2001);
			}
		} catch (ServerException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("haoz payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("haoz payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil
				.getMD5(data.get("mer_order_no") + data.get("amount") + payApiConfig.getSecretKey()).toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("Success".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("order_no"));
				result.setRealMoney(new BigDecimal(data.get("amount")));
				result.setBackThirdData("success");
			} else if ("Pending".equals(status)) {
				result.setPaySuccess(false);
				result.setThirdOrderNo(data.get("order_no"));
				result.setRealMoney(new BigDecimal(data.get("amount")));
				result.setBackThirdData("fail");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("haoz payment signature not match: {} - {}", oldSign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("fail");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method) {
		try {
			// ????????????
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("appid", payApiConfig.getMerchantId());
			params.put("mer_order_no", order.getOrderNo());
			params.put("currency", staticConfig.getDefaultCurrency().toString());
			params.put("amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("name", user.getName());
			params.put("phone", user.getMobilePhone());
			params.put("account", user.getCpfCode());
			params.put("bank", "CPF");
			params.put("card", user.getCpfCode());
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			// ??????
			String sign = EncryptUtil.getMD5(order.getOrderNo() + params.get("amount") + payApiConfig.getSecretKey())
					.toLowerCase();
			params.put("sign", sign);
			// ????????????
			String paramData = objectMapper.writeValueAsString(params);
			log.info("haoz withdraw param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("haoz withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("status").asText();
				if ("success".equals(code)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("order_no").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("msg").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(2002, "?????????????????????" + msg);
					} else {
						throw new ServerException(2002);
					}
				}
			} else {
				throw new ServerException(2002);
			}
		} catch (ServerException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("haoz withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("haoz withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(order.getOrderNo() + data.get("amount") + payApiConfig.getSecretKey())
				.toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("Success".equals(status)) {
				result.setState(1);
				result.setBackThirdData("success");
			} else if ("Pending".equals(status)) {
				result.setState(2);
				result.setBackThirdData("success");
			} else {
				result.setState(3);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("haoz withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("fail");
		}

		return result;
	}

}
