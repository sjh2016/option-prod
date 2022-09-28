package com.waben.option.thirdparty.service.pay.sskking;

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
public class SskkingPayService extends AbstractPaymentService {

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
		log.info("user: {}, sskking payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("version", "1.0");
			params.put("mch_id", payApiConfig.getMerchantId());
			params.put("notify_url", payApiConfig.getNotifyUrl());
			params.put("page_url", payApiConfig.getReturnUrl());
			params.put("mch_order_no", String.valueOf(orderId));
			if (method != null && method.getParam().equals("BANK")) {
				params.put("pay_type", "220");
				params.put("bank_code", "BCA");
			} else {
				params.put("pay_type", "222");
			}
			params.put("trade_amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).intValue());
			params.put("order_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			params.put("goods_name", "solar");
			params.put("page_url", payApiConfig.getReturnUrl());
			String signStr = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign_type", "MD5");
			params.put("sign", sign);
			// 请求上游
			String queryString = mapToQueryString(params);
			log.info("sskking payment param: {} ", queryString);
			Request postRequest = new Request.Builder()
					.url(payApiConfig.getOrderUrl()).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("sskking payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("respCode").asText();
				if ("SUCCESS".equals(code)) {
					Long orderNo = jsonNode.get("mchOrderNo").asLong();
					String thirdOrderNo = jsonNode.get("orderNo").asText();
					String payUrl = jsonNode.get("payInfo").asText();
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
					String msg = jsonNode.get("tradeMsg").asText();
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
			log.error("sskking payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("sskking payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		data.remove("signType");
		data.remove("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("tradeResult");
			if ("1".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("orderNo"));
				result.setRealMoney(new BigDecimal(data.get("amount")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("sskking payment signature not match: {} - {}", oldSign, checkSign);
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
			params.put("mch_transferId", order.getOrderNo());
			params.put("transfer_amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).intValue());
			params.put("apply_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			params.put("receive_name", user.getName());
			params.put("bank_code", order.getBankCode());
			params.put("receive_account", order.getBankCardId());
			params.put("back_url", payApiConfig.getNotifyUrl());
			String signStr = mapToQueryString(params);
			// 签名
			String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign_type", "MD5");
			params.put("sign", sign);
			// 请求上游
			String queryString = mapToQueryString(params);
			log.info("sskking withdraw param: {} ", queryString);
			Request postRequest = new Request.Builder()
					.url(payApiConfig.getOrderUrl()).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("sskking withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("respCode").asText();
				if ("SUCCESS".equals(code)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("tradeNo").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("errorMsg").asText();
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
			log.error("sskking withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("gyials withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		data.remove("signType");
		data.remove("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey())
				.toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("tradeResult");
			if ("1".equals(status)) {
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
