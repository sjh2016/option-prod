package com.waben.option.thirdparty.service.pay.gyials;

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
public class GyialsPayService extends AbstractPaymentService {

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
		log.info("user: {}, gyials payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", String.valueOf(orderId));
			params.put("pname", user.getName());
			params.put("pemail", user.getEmail());
			params.put("phone", user.getMobilePhone());
			params.put("order_amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).intValue());
			params.put("countryCode", "IDN");
			params.put("ccy_no", staticConfig.getDefaultCurrency().name());
			params.put("goods", "solar");
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("pageUrl", payApiConfig.getReturnUrl());
			if (method != null && method.getParam().equals("BANK")) {
				params.put("busi_code", "100401");
				params.put("bankCode", "BT");
			} else {
				params.put("busi_code", "100403");
			}
			String signStr = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("gyials payment param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("gyials payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("status").asText();
				if ("SUCCESS".equals(code)) {
					Long orderNo = jsonNode.get("mer_order_no").asLong();
					String thirdOrderNo = jsonNode.get("order_no").asText();
					String payUrl = jsonNode.get("order_data").asText();
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(orderNo);
					updateInfoReq.setThirdOrderNo(thirdOrderNo);
					paymentOrderAPI.updateThirdInfo(updateInfoReq);
					// 返回支付参数
					Map<String, Object> result = new HashMap<>();
					result.put("type", "4");
					result.put("content", payUrl);
					return result;
				} else {
					String msg = jsonNode.get("err_msg").asText();
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
			log.error("gyials payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("gyials payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("SUCCESS".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("order_no"));
				result.setRealMoney(new BigDecimal(data.get("order_amount")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("gyials payment signature not match: {} - {}", oldSign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("fail");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method) {
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", order.getOrderNo());
			params.put("acc_no", order.getBankCardId());
			params.put("acc_name", user.getName());
			params.put("ccy_no", staticConfig.getDefaultCurrency().name());
			params.put("order_amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).intValue());
			params.put("bank_code", order.getBankCode());
			params.put("mobile_no", order.getMobilePhone());
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("summary", "summary");
			String signStr = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("gyials withdraw param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("gyials withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("status").asText();
				if ("SUCCESS".equals(code)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("order_no").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("err_msg").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(2002, "上游提示错误：" + msg);
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
			log.error("gyials withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("gyials withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("SUCCESS".equals(status)) {
				result.setState(1);
				result.setBackThirdData("success");
			} else {
				result.setState(2);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("gyials withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("fail");
		}

		return result;
	}

}
