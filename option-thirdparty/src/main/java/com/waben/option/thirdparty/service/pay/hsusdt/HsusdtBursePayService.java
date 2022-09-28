package com.waben.option.thirdparty.service.pay.hsusdt;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.user.UserBurseAPI;
import com.waben.option.common.model.dto.payment.PayCallbackHandleResult;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO;
import com.waben.option.common.model.dto.payment.WithdrawCallbackHandleResult;
import com.waben.option.common.model.dto.payment.WithdrawOrderDTO;
import com.waben.option.common.model.dto.payment.WithdrawSystemResult;
import com.waben.option.common.model.dto.user.UserBurseDTO;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.PaymentService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class HsusdtBursePayService implements PaymentService {

	@Resource
	private ObjectMapper objectMapper;

	@Resource
	private OkHttpClient okHttpClient;

	@Resource
	private IdWorker idWorker;

	@Resource
	private UserBurseAPI userBurseAPI;

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
		log.info("user: {}, hsusdt burse payment request: {}", userId, request);
		// 查询用户钱包
		boolean needCreate = false;
		BurseTypeEnum burseType = null;
		if ("1".equals(method.getParam())) {
			burseType = BurseTypeEnum.TRC20;
		} else if ("2".equals(method.getParam())) {
			burseType = BurseTypeEnum.ERC20;
		} else {
			throw new ServerException(1002);
		}
		UserBurseDTO burse = userBurseAPI.query(userId, request.getReqCurrency(), burseType, payApiConfig.getId());
		String address = null;
		if (burse != null) {
			address = burse.getAddress();
		} else {
			try {
				// 构建参数
				Map<String, Object> params = new TreeMap<>();
				params.put("app_id", payApiConfig.getMerchantId());
				params.put("chain", method.getParam());
				params.put("number", 1);
				params.put("callback_url", payApiConfig.getNotifyUrl());
				params.put("time", System.currentTimeMillis());
				// 签名
				String sign = EncryptUtil.getMD5(mapToValueString(params) + payApiConfig.getSecretKey()).toLowerCase();
				params.put("token", sign);
				// 请求上游
				String paramString = JacksonUtil.encode(params);
				log.info("hsusdt burse payment param: {}", paramString);
				Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
						.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString))
						.build();
				Response response = okHttpClient.newCall(postRequest).execute();
				String json = response.body().string();
				log.info("hsusdt burse payment response: " + json);
				if (response.isSuccessful()) {
					JsonNode jsonNode = JacksonUtil.decodeToNode(json);
					String status = jsonNode.get("code").asText();
					if ("0".equals(status)) {
						String addresses = jsonNode.get("addresses").asText();
						address = JacksonUtil.decodeToNode(addresses).get(0).asText();
						needCreate = true;
					} else {
						String msg = jsonNode.get("status_mes").asText();
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
				log.error("hsusdt burse payment failed!", ex);
				throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
			}
		}
		// 返回支付参数
		Map<String, Object> result = new HashMap<>();
		result.put("type", "3");
		result.put("content", address);
		result.put("currency", request.getReqCurrency().name());
		result.put("burseType", burseType.name());
		// 创建钱包
		if (needCreate) {
			burse = new UserBurseDTO();
			burse.setUserId(userId);
			burse.setAddress(address);
			burse.setCurrency(request.getReqCurrency());
			burse.setBurseType(burseType);
			burse.setPayApiId(payApiConfig.getId());
			userBurseAPI.create(burse);
		}
		return result;
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("hsusdt burse payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String sign = jsonNode.get("token").asText();
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"token".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = EncryptUtil.getMD5(mapToValueString(params) + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("code").asText();
			if ("0".equals(status)) {
				UserBurseDTO burse = userBurseAPI.queryByAddress(jsonNode.get("address").asText());
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("money").asText()));
				result.setCashType(payApiConfig.getCashType());
				result.setThirdOrderNo(jsonNode.get("id").asText());
				result.setReqCurrency(burse.getCurrency());
				result.setUserId(burse.getUserId());
				result.setBurseAddress(burse.getAddress());
				result.setBurseType(burse.getBurseType().name());
			} else {
				result.setPaySuccess(false);
			}
			result.setBackThirdData("{\"code\":0}");
		} else {
			log.info("hsusdt burse payment signature not match: {} - {}", sign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("{\"code\":-1,\"message\":\"handle fail\"}");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method) {
		try {
			// 构建参数
			Map<String, Object> params = new TreeMap<>();
			params.put("app_id", payApiConfig.getMerchantId());
			params.put("time", System.currentTimeMillis());
			params.put("money", request.getRealNum());
			params.put("client_order_id", order.getOrderNo());
			params.put("callback_url", payApiConfig.getNotifyUrl());
			params.put("bank_id", method.getParam());
			params.put("user_account", order.getBurseAddress());
			// 签名
			String sign = EncryptUtil.getMD5(mapToValueString(params) + payApiConfig.getSecretKey()).toLowerCase();
			params.put("token", sign);
			// 请求上游
			String paramString = JacksonUtil.encode(params);
			log.info("hsusdt burse withdraw param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("hsusdt burse withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("code").asText();
				if ("0".equals(status)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("id").asText());
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
			log.error("hsusdt burse withdraw failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("hsusdt burse withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String sign = jsonNode.get("token").asText();
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"token".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = EncryptUtil.getMD5(mapToValueString(params) + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("status").asText();
			if ("3".equals(status)) {
				result.setState(1);
			} else if ("4".equals(status)) {
				result.setState(2);
			} else {
				result.setState(3);
			}
			result.setBackThirdData("{\"code\":0}");
		} else {
			log.info("hsusdt burse withdraw signature not match: {} - {}", sign, checkSign);
			result.setBackThirdData("{\"code\":-1,\"message\":\"handle fail\"}");
		}
		return result;
	}

}
