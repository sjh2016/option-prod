package com.waben.option.thirdparty.service.pay.brapix;

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
public class BrapixPayService extends AbstractPaymentService {

	@Resource
	private OkHttpClient okHttpClient;

	@Resource
	private PaymentOrderAPI paymentOrderAPI;

	@Resource
	private StaticConfig staticConfig;

	@Resource
	private ObjectMapper objectMapper;

	private String map(Map<String, Object> map) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			if (!"sign".equals(entry.getKey()) && !org.springframework.util.StringUtils.isEmpty(entry.getValue())) {
				builder.append(entry.getValue());
			}
		}
		return builder.toString();
	}

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method,
			PaymentPassagewayDTO passageway) {
		log.info("user: {}, brapix payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("merchantId", payApiConfig.getMerchantId());
			params.put("tradeNo", String.valueOf(orderId));
			params.put("currency", staticConfig.getDefaultCurrency().name());
			params.put("payAmount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("payWay", "pix");
			params.put("resultType", "json");
			String signStr = this.map(params);
			String s = signStr + payApiConfig.getSecretKey();
			// 签名
			String sign = EncryptUtil.getMD5(s).toLowerCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("brapix payment param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("brapix payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("code").asText();
				if ("1".equals(code)) {
					String payUrl = jsonNode.get("url").asText();
					// 返回支付参数
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
			log.error("brapix payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("brapix payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + payApiConfig.getSecretKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("oStatus");
			if ("2".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("tradeNo"));
				result.setRealMoney(new BigDecimal(data.get("payAmount")));
				result.setBackThirdData("success");
			} else if ("1".equals(status)) {
				result.setPaySuccess(false);
				result.setThirdOrderNo(data.get("tradeNo"));
				result.setRealMoney(new BigDecimal(data.get("payAmount")));
				result.setBackThirdData("fail");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("brapix payment signature not match: {} - {}", oldSign, checkSign);
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
			params.put("merchantId", payApiConfig.getMerchantId());
			params.put("tradeNo", order.getOrderNo());
			params.put("payAmount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("payWay", "pix");
			params.put("currency", staticConfig.getDefaultCurrency().name());
			// 签名
			String signStr = this.map(params);
			String s = signStr + payApiConfig.getSecretKey();
			String sign = EncryptUtil.getMD5(s).toLowerCase();
			if (method != null && method.getParam().equals("BANK")) {
				params.put("bankAG", "BANK");
				params.put("bankCC", order.getBankCardId());
				params.put("bankName", order.getBankName());
			} else {
				params.put("pixType", "cpf");
				params.put("pixKey", user.getCpfCode());
				params.put("pixCity", user.getCountryCode());
			}
			params.put("payeeName", user.getName().trim());
			params.put("payeeCPF", user.getCpfCode());
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("brapix withdraw param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("brapix withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("code").asText();
				if ("1".equals(code)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("msg").asText();
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
			log.error("brapix withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("brapix withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + payApiConfig.getSecretKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("oStatus");
			if ("3".equals(status)) {
				result.setState(1);
				result.setBackThirdData("success");
			} else if ("1".equals(status) || "2".equals(status) || "4".equals(status) || "5".equals(status)) {
				result.setState(2);
				result.setBackThirdData("fail");
			} else {
				result.setState(3);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("brapix withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("fail");
		}

		return result;
	}

}
