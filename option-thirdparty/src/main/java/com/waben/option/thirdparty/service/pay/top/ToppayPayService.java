package com.waben.option.thirdparty.service.pay.top;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
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
public class ToppayPayService implements PaymentService {

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

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	@Override
	public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
			PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
		log.info("user: {}, toppay payment request: {}", userId, request);
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
			params.put("merchantCode", payApiConfig.getMerchantId());
			params.put("orderNum", String.valueOf(orderId));
			params.put("payMoney", String.valueOf(request.getReqMoney().intValue()));
			params.put("productDetail", "recharge" + params.get("payMoney"));
			params.put("name", "payer");
			params.put("email", "payer@gmail.com");
			params.put("phone", "6281999999999");
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			params.put("redirectUrl", payApiConfig.getReturnUrl());
			params.put("dateTime", formatter.format(LocalDateTime.now()));
			if (StringUtils.isBlank(method.getParam())) {
				params.put("method", method.getParam());
			}
			// 签名
			String signStr = mapToValueString(params);
			String sign = privateEncrypt(signStr, getPrivateKey(payApiConfig.getPrivateKey()));
			params.put("sign", sign);
			// 请求上游
			String paramString = JacksonUtil.encode(params);
			log.info("toppay payment param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("toppay payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("platRespCode").asText();
				if ("SUCCESS".equals(status)) {
					String payUrl = jsonNode.get("url").asText();
					String thirdOrderNo = jsonNode.get("platOrderNum").asText();
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
					String msg = jsonNode.get("platRespMessage").asText();
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
			log.error("toppay payment failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("toppay payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String sign = jsonNode.get("platSign").asText();
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"platSign".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = "";
		try {
			checkSign = publicDecrypt(sign, getPublicKey(payApiConfig.getPublicKey()));
		} catch (Exception ex) {
			log.error("public decrypt exception!");
		}
		if (mapToValueString(params).equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("code").asText();
			if ("00".equals(status)) {
				result.setPaySuccess(true);
				result.setRealMoney(new BigDecimal(jsonNode.get("payMoney").asText()));
			} else {
				result.setPaySuccess(false);
			}
			result.setBackThirdData("SUCCESS");
		} else {
			log.info("toppay payment signature not match: {} - {}", sign, checkSign);
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
			params.put("merchantCode", payApiConfig.getMerchantId());
			params.put("orderNum", order.getOrderNo());
			params.put("money", String.valueOf(request.getRealNum().intValue()));
			params.put("feeType", "1");
			params.put("dateTime", formatter.format(LocalDateTime.now()));
			params.put("name", order.getName());
			params.put("number", order.getBankCardId());
			params.put("bankCode", order.getBankCode());
			params.put("description", "withdraw" + params.get("money"));
			params.put("notifyUrl", payApiConfig.getNotifyUrl());
			// 签名
			String signStr = mapToValueString(params);
			String sign = privateEncrypt(signStr, getPrivateKey(payApiConfig.getPrivateKey()));
			params.put("sign", sign);
			// 请求上游
			String paramString = JacksonUtil.encode(params);
			log.info("toppay withdraw param: {}", paramString);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("toppay withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String status = jsonNode.get("platRespCode").asText();
				if ("SUCCESS".equals(status)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("platOrderNum").asText());
					wr.setThirdRespMsg(json);
					wr.setImmediateSuccess(false);
					return wr;
				} else {
					String msg = jsonNode.get("platRespMessage").asText();
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
			log.error("toppay withdraw failed!", ex);
			throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("toppay withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String body = data.get("body");
		JsonNode jsonNode = JacksonUtil.decodeToNode(body);
		// 验证签名
		String sign = jsonNode.get("platSign").asText();
		// 对参数排序
		Map<String, Object> params = new TreeMap<>();
		Iterator<String> iter = jsonNode.fieldNames();
		while (iter.hasNext()) {
			String param = iter.next();
			String value = jsonNode.get(param).asText();
			if (!"platSign".equals(param)) {
				params.put(param, value);
			}
		}
		String checkSign = "";
		try {
			checkSign = publicDecrypt(sign, getPublicKey(payApiConfig.getPublicKey()));
		} catch (Exception ex) {
			log.error("public decrypt exception!");
		}
		if (mapToValueString(params).equalsIgnoreCase(checkSign)) {
			String status = jsonNode.get("status").asText();
			if ("2".equals(status)) {
				result.setState(1);
			} else if ("4".equals(status)) {
				result.setState(2);
			} else {
				result.setState(3);
			}
			result.setBackThirdData("SUCCESS");
		} else {
			log.info("toppay withdraw signature not match: {} - {}", sign, checkSign);
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
	 * 公钥解密
	 * 
	 * @param data
	 * @param publicKey
	 * @return
	 */
	private static String publicDecrypt(String data, RSAPublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data),
					publicKey.getModulus().bitLength()), "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException("解密字符串[" + data + "]时遇到异常", e);
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

	/**
	 * 得到公钥
	 * 
	 * @param publicKey 密钥字符串（经过base64编码）
	 * @throws Exception
	 */
	private static RSAPublicKey getPublicKey(String publicKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		// 通过X509编码的Key指令获得公钥对象
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
		RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
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
