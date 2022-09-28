package com.waben.option.thirdparty.service.pay.wouzs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
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
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
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
public class WouzsPayService implements PaymentService {

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

	private DecimalFormat format = new DecimalFormat("0.00");

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
		log.info("user: {}, wouzs payment request: {}", userId, request);
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
			Map<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", String.valueOf(orderId));
			params.put("pname", "payer");
			params.put("pemail", "payer@gmail.com");
			String mobile = user.getMobilePhone();
			if (StringUtils.isBlank(mobile)) {
				params.put("phone", "99999999999");
			} else {
				params.put("phone", mobile);
			}
			params.put("order_amount", format.format(request.getReqMoney().setScale(0, RoundingMode.DOWN)));
			params.put("ccy_no", request.getReqCurrency().name());
			params.put("busi_code", method.getParam());
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("pageUrl", payApiConfig.getReturnUrl());
			params.put("bankCode", "BCA");
			// 签名
			String signStr = mapToQueryString(params);
			String sign = URLEncoder.encode(privateEncrypt(signStr, getPrivateKey(payApiConfig.getPrivateKey())),
					"UTF-8");
			params.put("sign", sign);
			// 请求上游
			String paramString = JacksonUtil.encode(params);
			log.info("wouzs payment param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("wouzs payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				if ("SUCCESS".equals(status)) {
					String payUrl = jsonNode.get("order_data").asText();
					String thirdOrderNo = jsonNode.get("order_no").asText();
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
					String msg = jsonNode.get("err_msg").asText();
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
			log.error("wouzs payment failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("wouzs payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"sign".equals(param) && !StringUtils.isBlank(value)) {
				params.put(param, value);
			}
		}
		// 验证签名
		String sign = jsonNode.get("sign").asText();
		String checkSign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getSecretKey());
		if (sign != null && sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("status").asText();
			if ("SUCCESS".equals(status)) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("pay_amount").asText()));
			} else {
				result.setPaySuccess(false);
			}
			result.setBackThirdData("SUCCESS");
		} else {
			log.info("wouzs payment signature not match: {} - {}", sign, checkSign);
			result.setPaySuccess(false);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

	@Override
	public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method) {
		try {
			// 构建参数
			Map<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", order.getOrderNo());
			params.put("acc_no", order.getBankCardId());
			params.put("acc_name", order.getName());
			params.put("ccy_no", order.getTargetCurrency().name());
			params.put("order_amount", format.format(request.getRealNum().setScale(0, RoundingMode.DOWN)));
			params.put("bank_code", order.getBankCode());
			params.put("mobile_no", "9999999999");
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("summary", order.getOrderNo());
			if (staticConfig.getDefaultCurrency() == CurrencyEnum.INR) {
				if (StringUtils.isBlank(order.getBranchName())) {
					throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_IFSC_EMPTY);
				}
				params.put("province", order.getBranchName());
			}
			// 签名
			String signStr = mapToQueryString(params);
			String sign = URLEncoder.encode(privateEncrypt(signStr, getPrivateKey(payApiConfig.getPrivateKey())),
					"UTF-8");
			params.put("sign", sign);
			// 请求上游
			String paramString = JacksonUtil.encode(params);
			log.info("wouzs withdraw param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("wouzs withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("status").asText();
				if ("SUCCESS".equals(status)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("order_no").asText());
					wr.setThirdRespMsg(jsonNode.get("order_no").asText());
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("err_msg").asText();
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
			log.error("wouzs withdraw failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("wouzs withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"sign".equals(param) && !StringUtils.isBlank(value)) {
				params.put(param, value);
			}
		}
		// 验证签名
		String sign = jsonNode.get("sign").asText();
		String checkSign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getSecretKey());
		if (sign != null && sign.equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("status").asText();
			if ("SUCCESS".equals(status)) {
				result.setState(1);
			} else if ("FAIL".equals(status)) {
				result.setState(2);
			} else {
				result.setState(3);
			}
			result.setBackThirdData("SUCCESS");
		} else {
			log.info("wouzs withdraw signature not match: {} - {}", sign, checkSign);
			result.setBackThirdData("FAIL");
		}
		return result;
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 * @param privateKey
	 * @return
	 */
	private static String privateEncrypt(String data, RSAPrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			return Base64.encodeBase64String(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes("UTF-8"),
					privateKey.getModulus().bitLength()));
		} catch (Exception e) {
			throw new RuntimeException("加密字符串[" + data + "]时遇到异常", e);
		}
	}

	/**
	 * 得到私钥
	 * 
	 * @param privateKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	private static RSAPrivateKey getPrivateKey(String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过PKCS#8编码的Key指令获得私钥对象
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
		RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
		return key;
	}

	private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
		int maxBlock = 0;
		if (opmode == Cipher.DECRYPT_MODE) {
			maxBlock = keySize / 8;
		} else {
			maxBlock = keySize / 8 - 11;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] buff;
			int i = 0;
			try {
				while (datas.length > offSet) {
					if (datas.length - offSet > maxBlock) {
						buff = cipher.doFinal(datas, offSet, maxBlock);
					} else {
						buff = cipher.doFinal(datas, offSet, datas.length - offSet);
					}
					out.write(buff, 0, buff.length);
					i++;
					offSet = i * maxBlock;
				}
			} catch (Exception e) {
				throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
			}
			byte[] resultDatas = out.toByteArray();
			return resultDatas;
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

}
