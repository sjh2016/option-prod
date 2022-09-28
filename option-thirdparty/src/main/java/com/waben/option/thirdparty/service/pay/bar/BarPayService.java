package com.waben.option.thirdparty.service.pay.bar;

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

/**
 * @author: Peter
 * @date: 2021/7/6 13:33
 */
@Slf4j
@Service
public class BarPayService extends AbstractPaymentService {

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
		log.info("user: {}, bar payment request: {}", userId, request);
		Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
		try {
			// 构建参数
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", String.valueOf(orderId));
			params.put("pname", user.getName() + user.getSurname());
			params.put("pemail", user.getEmail());
			params.put("phone", user.getMobilePhone());
			params.put("cpf", user.getCpfCode());
			params.put("order_amount", request.getReqMoney()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("country_code", "BRA");
			params.put("cyy_no", "IDR");
			params.put("pay_type", "PIX");
			params.put("notify_url", payApiConfig.getNotifyUrl());
			// 签名
			String sign = EncryptUtil.getMD5(payApiConfig.getMerchantId() + orderId + payApiConfig.getSecretKey())
					.toLowerCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("bar payment param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("bar payment response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("code").asText();
				if ("1".equals(code)) {
					Long orderNo = jsonNode.get("mer_order_no").asLong();
					String thirdOrderNo = jsonNode.get("order_number").asText();
					String payUrl = jsonNode.get("pay_url").asText();
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
			log.error("bar payment failed!", ex);
			throw new ServerException(2001);
		}
	}

	@Override
	public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
		log.info("bar payment callback: {}", data);
		PayCallbackHandleResult result = new PayCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil
				.getMD5(data.get("mer_no") + data.get("mer_order_no") + payApiConfig.getSecretKey()).toLowerCase();
		if (oldSign.equalsIgnoreCase(checkSign)) {
			String status = data.get("order_status");
			if ("4".equals(status)) {
				result.setPaySuccess(true);
				result.setThirdOrderNo(data.get("order_no"));
				result.setRealMoney(new BigDecimal(data.get("pay_amount")));
				result.setBackThirdData("success");
			} else {
				result.setPaySuccess(false);
				result.setBackThirdData("fail");
			}
		} else {
			log.info("bar payment signature not match: {} - {}", oldSign, checkSign);
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
			Map<String, String> baseMap = buildBankCodeMap();
			String bankEncrypt = baseMap.get(order.getBankName());
			if (StringUtils.isEmpty(bankEncrypt))
				throw new ServerException(2025);
			TreeMap<String, Object> params = new TreeMap<>();
			params.put("mer_no", payApiConfig.getMerchantId());
			params.put("mer_order_no", order.getOrderNo());
			params.put("order_amount", request.getRealNum()
					.setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
			params.put("pay_type", "PIX");
			params.put("cyy_no", "IDR");
			params.put("cpf", user.getCpfCode());
			params.put("acc_no", order.getBankCardId());
			params.put("acc_name", order.getBankName());
			params.put("bank_code", baseMap.get(order.getBankName()));
			params.put("bank_encrypt", baseMap.get(order.getBankName()));
			params.put("notifyurl", payApiConfig.getNotifyUrl());
			String sign = EncryptUtil
					.getMD5(payApiConfig.getMerchantId() + order.getOrderNo() + payApiConfig.getSecretKey())
					.toLowerCase();
			params.put("sign", sign);
			// 请求上游
			String paramData = objectMapper.writeValueAsString(params);
			log.info("bar withdraw param: {} ", paramData);
			Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
					.post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
			Response response = okHttpClient.newCall(postRequest).execute();
			String json = response.body().string();
			log.info("bar withdraw response: " + json);
			if (response.isSuccessful()) {
				JsonNode jsonNode = JacksonUtil.decodeToNode(json);
				String code = jsonNode.get("code").asText();
				if ("1".equals(code)) {
					WithdrawSystemResult wr = new WithdrawSystemResult();
					wr.setThirdOrderNo(jsonNode.get("order_number").asText());
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
			log.error("bar withdraw failed!", ex);
			throw new ServerException(2002);
		}
	}

	@Override
	public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
			Map<String, String> data) {
		log.info("bar withdraw callback: {}", data);
		WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
		String oldSign = data.get("sign");
		String checkSign = EncryptUtil
				.getMD5(payApiConfig.getMerchantId() + order.getOrderNo() + payApiConfig.getSecretKey()).toLowerCase();
		if ("1".equals(data.get("code"))) {
			if (oldSign.equalsIgnoreCase(checkSign)) {
				String status = data.get("order_status");
				if ("4".equals(status)) {
					result.setState(1);
					result.setBackThirdData("success");
				} else if ("3".equals(status) || "-1".equals(status) || "2".equals(status)) {
					result.setState(2);
					result.setBackThirdData("success");
				} else {
					result.setState(3);
					result.setBackThirdData("fail");
				}
			} else {
				log.info("bar withdraw signature not match: {} - {}", oldSign, checkSign);
				result.setBackThirdData("fail");
			}
		}
		return result;
	}

	private Map<String, String> buildBankCodeMap() {
		Map<String, String> baseMap = new HashMap<>();
		baseMap.put("Banco Caixa Economica Federal", "0104");
		baseMap.put("Banco do Brasil", "0001");
		baseMap.put("Banco Bradesco", "0237");
		baseMap.put("Banco Itau", "0341");
		baseMap.put("Banco Santander", "0033");
		baseMap.put("AGIPLAN", "0121");
		baseMap.put("Banco BMG", "0318");
		baseMap.put("Banco Bonsucesso", "0218");
		baseMap.put("Banco BRB de Brasília", "0070");
		baseMap.put("Banco Citibank", "0745");
		baseMap.put("Banco Cooperativa do Brasil", "0756");
		baseMap.put("Banco Cooperativa Sicred", "0748");
		baseMap.put("Banco da Amazonia", "0003");
		baseMap.put("BANCO DAYCOVAL S.A.", "0707");
		baseMap.put("Banco do Estado de Santa Catarina", "0087");
		baseMap.put("Banco do Estado de Sergipe", "0047");
		baseMap.put("Banco do Estado do Para", "0037");
		baseMap.put("Banco do Estado do Rio Grande do Sul", "0041");
		baseMap.put("Banco HSBC", "0399");
		baseMap.put("Banco Indusval", "0653");
		baseMap.put("Banco Intermedium S.A.", "0077");
		baseMap.put("Banco Mercantil do Brasil", "0389");
		baseMap.put("Banco Nubank", "0260");
		baseMap.put("Banco Original S.A.", "0212");
		baseMap.put("Banco Rendimento", "0633");
		baseMap.put("Banco Safra", "0422");
		baseMap.put("Banco Votorantim", "0655");
		baseMap.put("Banestes S\\A - Banco do Estado do Espírito Santo", "0021");
		baseMap.put("Bank of America", "0755");
		baseMap.put("Cooperativa Central de Crédito Urbano Cecred", "0085");
		baseMap.put("Cooperativa Unicred Central SP", "0090");
		baseMap.put("Cooperativa Unicred de Sete Lagoas", "0136");
		baseMap.put("CRESOL Confederacao", "0133");
		baseMap.put("Parana Banco", "0254");
		baseMap.put("Unicred Norte do Paraná", "0084");
		return baseMap;
	}
}
