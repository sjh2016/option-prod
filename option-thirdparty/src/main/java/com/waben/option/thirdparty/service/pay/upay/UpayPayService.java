package com.waben.option.thirdparty.service.pay.upay;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class UpayPayService implements PaymentService {

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
		log.info("user: {}, upay payment request: {}", userId, request);
		// ????????????????????????????????????????????????
		if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getEmail())
				|| StringUtils.isBlank(user.getMobilePhone())) {
			throw new ServerException(2000);
		}
		// ????????????
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
			// ????????????
			Map<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("order_no", String.valueOf(orderId));
			if ("1081911".equals(payApiConfig.getMerchantId())) {
				// ??????????????????100
				params.put("order_amount", "100");
			} else {
				params.put("order_amount", request.getReqMoney().toPlainString());
			}
			params.put("payname", user.getName());
			params.put("payemail", user.getEmail());
			params.put("payphone", user.getMobilePhone());
			params.put("currency", request.getReqCurrency().name());
			params.put("paytypecode", method.getParam());
			params.put("method", "trade.create");
			params.put("returnurl", payApiConfig.getNotifyUrl());
			// ??????
			String sign = EncryptUtil.getMD5(mapToQueryString(params) + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign", sign);
			// ????????????
			String paramString = JacksonUtil.encode(params);
			log.info("upay payment param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("upay payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				if ("success".equals(status)) {
					String payUrl = jsonNode.get("order_data").asText();
					// ??????????????????
					PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
					updateInfoReq.setId(orderId);
					updateInfoReq.setThirdOrderNo("UpNoResponse" + orderId);
					paymentOrderAPI.updateThirdInfo(updateInfoReq);
					// ??????????????????
					Map<String, Object> result = new HashMap<>();
					result.put("type", "4");
					result.put("content", payUrl);
					return result;
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
			log.error("upay payment failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("upay payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// ????????????
		String sign = jsonNode.get("sign").asText();
		// ???????????????
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"sign".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = EncryptUtil.getMD5(mapToQueryString(params) + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("status").asText();
			if ("success".equals(status)) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("order_realityamount").asText()));
			} else {
				result.setPaySuccess(false);
			}
			result.setBackThirdData("ok");
		} else {
			log.info("upay payment signature not match: {} - {}", sign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method) {
		try {
			// ????????????
			Map<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("order_no", order.getOrderNo());
			params.put("method", "fund.apply");
			params.put("order_amount", String.valueOf(request.getRealNum()));
			params.put("currency", order.getTargetCurrency().name());
			params.put("acc_code", order.getBankCode());
			params.put("acc_name", order.getName());
			params.put("acc_no", order.getBankCardId());
			params.put("returnurl", payApiConfig.getNotifyUrl());
			// ??????
			String sign = EncryptUtil.getMD5(mapToQueryString(params) + payApiConfig.getSecretKey()).toLowerCase();
			params.put("sign", sign);
			// ????????????
			String paramString = JacksonUtil.encode(params);
			log.info("upay withdraw param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("upay withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				if ("success".equals(status)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("sys_no").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("status_mes").asText();
					if (!StringUtils.isBlank(msg)) {
						throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL_WITH_MSG,
								"?????????????????????" + msg);
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
			log.error("upay withdraw failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("upay withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// ????????????
		String sign = jsonNode.get("sign").asText();
		// ???????????????
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"sign".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = EncryptUtil.getMD5(mapToQueryString(params) + payApiConfig.getSecretKey()).toLowerCase();
		if (sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("result").asText();
			if ("success".equals(status)) {
				result.setState(1);
			} else if ("fail".equals(status)) {
				result.setState(2);
			} else {
				result.setState(3);
			}
			result.setBackThirdData("ok");
		} else {
			log.info("upay withdraw signature not match: {} - {}", sign, checkSign);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

}
