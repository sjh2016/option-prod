package com.waben.option.thirdparty.service.pay.fuyou;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
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
public class FuyouPayService extends AbstractPaymentService {

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
		log.info("user: {}, fuyou payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mch_id", payApiConfig.getMerchantId());
			params.put("pass_code", "101");
			params.put("subject", "power");
			params.put("out_trade_no", String.valueOf(orderId));
			params.put("money", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("client_ip", userIp);
			params.put("notify_url", payApiConfig.getNotifyUrl());
			params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			String signStr = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("fuyou payment param: {}", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("fuyou payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("code").asText();
				if ("0".equals(code)) {
					TreeMap<String, String> map = objectMapper.readValue(
							objectMapper.writeValueAsString(jsonNode.get("data")),
							new TypeReference<TreeMap<String, String>>() {
							});
					String thirdOrderNo = map.get("trade_no");
					String payUrl = map.get("pay_url");
					String orderNo = map.get("out_trade_no");
					// 更新上游单号
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(Long.valueOf(orderNo));
					updateInfoReq.setThirdOrderNo(thirdOrderNo);
					paymentOrderAPI.updateThirdInfo(updateInfoReq);
					// 返回支付参数
					Map<String, Object> result = new HashMap<>();
					result.put("type", "1");
					result.put("content", payUrl);
					return result;
				} else {
					String msg = jsonNode.get("retMsg").asText();
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
			log.error("fuyou payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("fuyou payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getPrivateKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("state");
			if ("2".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("trade_no"));
				result.setRealMoney(new BigDecimal(data.get("money")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("fuyou payment signature not match: {} - {}", oldSign, checkSign);
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
			params.put("mch_id", payApiConfig.getMerchantId());
			params.put("mchTransNo", order.getOrderNo());
			params.put("money", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("acct_name", order.getName());
			params.put("bank_name", order.getBankName());
			params.put("acct_no", order.getBankCardId());
			params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			// 签名
			String s = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(s + "&key=" + "" + payApiConfig.getPrivateKey()).toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("fuyou withdraw param: {}", paramData);
			Request postRequest = new Request.Builder()
					.url(payApiConfig.getOrderUrl()).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), paramData))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("fuyou withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String retCode = jsonNode.get("code").asText();
				if ("0".equals(retCode)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("retMsg").asText();
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
			log.error("fuyou withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("fuyou withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getPrivateKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("2".equals(status)) {
				result.setState(1);
				result.setBackThirdData("success");
			} else if ("3".equals(status)) {
				result.setState(2);
				result.setBackThirdData("success");
			} else {
				result.setState(3);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("fuyou withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

}
