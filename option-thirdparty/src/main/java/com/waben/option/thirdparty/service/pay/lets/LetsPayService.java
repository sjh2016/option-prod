package com.waben.option.thirdparty.service.pay.lets;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.PayCallbackHandleResult;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
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
public class LetsPayService extends AbstractPaymentService {

	@Resource
	private OkHttpClient okHttpClient;

	@Resource
	private PaymentOrderAPI paymentOrderAPI;

	@Resource
	private StaticConfig staticConfig;

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
		log.info("user: {}, lets payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			String goods = "email:" + user.getEmail() + "/name:" + user.getName() + user.getSurname() + "/phone:"
					+ user.getMobilePhone() + "/cpf:" + user.getCpfCode();
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mchId", payApiConfig.getMerchantId());
			params.put("orderNo", String.valueOf(orderId));
			params.put("amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("product", "baxipix");
			params.put("bankcode", "all");
			params.put("goods", goods);
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("returnUrl", payApiConfig.getReturnUrl());
			// 签名
			String sign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getPrivateKey())
					.toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String queryString = mapToQueryString(params);
			log.info("lets payment param: {}", queryString);
			Request postRequest = new Request.Builder()
					.url(payApiConfig.getOrderUrl()).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("lets payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("retCode").asText();
				if ("SUCCESS".equals(code)) {
					String thirdOrderNo = jsonNode.get("platOrder").asText();
					String payUrl = jsonNode.get("payUrl").asText();
					Long orderNo = jsonNode.get("orderNo").asLong();
					// 更新上游单号
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(orderNo);
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
			log.error("lets payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("lets payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getPrivateKey())
				.toUpperCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("status");
			if ("2".equals(status)) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(data.get("amount")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("lets payment signature not match: {} - {}", oldSign, checkSign);
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
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("type", "api");
			params.put("mchId", payApiConfig.getMerchantId());
			params.put("mchTransNo", order.getOrderNo());
			params.put("amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("accountName", order.getBankName());
			params.put("accountNo", order.getBankCardId());
			params.put("bankCode", "pix");
			params.put("remarkInfo", "email:" + user.getEmail() + "/phone:" + user.getMobilePhone());
			// 签名
			String sign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getPrivateKey())
					.toUpperCase();
			params.put("sign", sign);
			// 请求上游
			String queryString = mapToQueryString(params);
			log.info("lets withdraw param: {}", queryString);
			Request postRequest = new Request.Builder()
					.url(payApiConfig.getOrderUrl()).post(RequestBody
							.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
					.build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("lets withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String retCode = jsonNode.get("retCode").asText();
				if ("SUCCESS".equals(retCode)) {
					long orderNo = jsonNode.get("mchTransNo").asLong();
					if (orderNo == order.getId()) {
						WithdrawSystemResult wr = new WithdrawSystemResult();
						wr.setThirdOrderNo(jsonNode.get("platOrder").asText());
						wr.setThirdRespMsg(json);
						wr.setImmediateSuccess(false);
						return wr;
					}
					throw new ServerException(2002, String.format("单不订一致：%s|%s", orderNo, order.getId()));
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
			log.error("lets withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("lets withdraw callback: {}", data);
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
			log.info("lets withdraw signature not match: {} - {}", oldSign, checkSign);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

}
