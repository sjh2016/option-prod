package com.waben.option.thirdparty.service.pay.king;

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

/**
 * @author: Peter
 * @date: 2021/7/6 13:33
 */
@Slf4j
@Service
public class KingPayService extends AbstractPaymentService {

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
		log.info("user: {}, king payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mch_no", payApiConfig.getMerchantId());
			params.put("out_trade_no", String.valueOf(orderId));
			params.put("amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			if (method != null && method.getParam().equals("BANK")) {
				params.put("type", "BANK");
			} else {
				params.put("type", "PIX");
			}
			params.put("notify_url", payApiConfig.getNotifyUrl());
			params.put("ext", "all");
			params.put("version", "1.0.0");
			params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			params.put("realname", user.getName() + user.getSurname());
			params.put("account", user.getMobilePhone());
			// 签名
			String signStr = mapToQueryString(params);
			log.info("king payment sign str :{}", signStr);
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("king payment param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("king payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("errcode").asText();
				if ("0".equals(code)) {
					TreeMap<String, String> map = objectMapper.readValue(
							objectMapper.writeValueAsString(jsonNode.get("data")),
							new TypeReference<TreeMap<String, String>>() {
							});
					String checkSign = EncryptUtil.getMD5(mapToString(map) + "&key=" + payApiConfig.getSecretKey())
							.toUpperCase();
					if (checkSign.equalsIgnoreCase(map.get("sign"))) {
						String orderNo = map.get("out_trade_no");
						String thirdOrderNo = map.get("platform_order_no");
						String payUrl = map.get("url");
						PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
						updateInfoReq.setId(Long.valueOf(orderNo));
						updateInfoReq.setThirdOrderNo(thirdOrderNo);
						paymentOrderAPI.updateThirdInfo(updateInfoReq);
						// 返回支付参数
						Map<String, Object> result = new HashMap<>();
						result.put("type", "1");
						result.put("content", payUrl);
						return result;
					}
					throw new ServerException(2001, "验签失败");
				} else {
					String msg = jsonNode.get("errmsg").asText();
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
			log.error("king payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("king payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("3".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("platform_order_no"));
				result.setRealMoney(new BigDecimal(data.get("amount")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("king payment signature not match: {} - {}", oldSign, checkSign);
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
			params.put("mch_no", payApiConfig.getMerchantId());
			params.put("out_trade_no", order.getOrderNo());
			params.put("amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("notify_url", payApiConfig.getNotifyUrl());
			params.put("ext", "all");
			params.put("version", "1.0.0");
			params.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			params.put("name", user.getName() + user.getSurname());
			if (method != null && method.getParam().equals("BANK")) {
				params.put("type", "BANK");
				params.put("account", order.getBankCardId());
				params.put("bank_name", order.getBankName());
			} else {
				params.put("type", "PIX");
				params.put("account", order.getBurseAddress());
			}
			String signStr = mapToQueryString(params);
			log.info("king withdraw sign str :{}", signStr);
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("king withdraw param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("king withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("errcode").asText();
				if ("0".equals(code)) {
					TreeMap<String, String> map = objectMapper.readValue(
							objectMapper.writeValueAsString(jsonNode.get("data")),
							new TypeReference<TreeMap<String, String>>() {
							});
					String checkSign = EncryptUtil.getMD5(mapToString(map) + "&key=" + payApiConfig.getSecretKey())
							.toUpperCase();
					if (checkSign.equalsIgnoreCase(map.get("sign"))) {
						WithdrawSystemResult wr = new WithdrawSystemResult();
						wr.setThirdOrderNo(map.get("platform_order_no"));
						wr.setThirdRespMsg(json);
						wr.setImmediateSuccess(false);
						return wr;
					}
					throw new ServerException(2001, "验签失败");
				} else {
					String msg = jsonNode.get("errmsg").asText();
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
			log.error("king withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("king withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("3".equals(status)) {
				result.setState(1);
				result.setBackThirdData("success");
			} else if ("4".equals(status) || "5".equals(status)) {
				result.setState(2);
				result.setBackThirdData("success");
			} else {
				result.setState(3);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("king withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("fail");
		}
		return result;
	}
}
