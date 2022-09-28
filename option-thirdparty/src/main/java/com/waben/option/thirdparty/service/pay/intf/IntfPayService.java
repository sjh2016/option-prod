package com.waben.option.thirdparty.service.pay.intf;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.pay.AbstractPaymentService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class IntfPayService extends AbstractPaymentService {

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
                                   PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, intf payment request: {}", userId, request);
        Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
        try {
            // 构建参数
            TreeMap<String, Object> params = new TreeMap<>();
            params.put("partner", payApiConfig.getMerchantId());
            params.put("sign_type", "MD5");
            params.put("trade_no", String.valueOf(orderId));
            params.put("trade_amount", request.getReqMoney().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
            params.put("country_code", "62");
            params.put("notify_url", payApiConfig.getNotifyUrl());
            params.put("open_url", payApiConfig.getReturnUrl());
            if (method != null && method.getParam().equals("BANK")) {
                params.put("pay_type", "62201");
            } else {
                params.put("pay_type", "62202");
            }
            params.put("goods", "solar");
            params.put("name", user.getName());
            params.put("email", user.getEmail());
            params.put("phone", user.getMobilePhone());
            String signStr = mapToQueryString(params);
            // 签名
            String sign = EncryptUtil.getMD5(signStr + "&key=" +payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign", sign);
            // 请求上游
            String paramData = objectMapper.writeValueAsString(params);
            log.info("intf payment param: {}", paramData);
            Request postRequest = new Request.Builder()
                    .url(payApiConfig.getOrderUrl()).post(RequestBody
                            .create(MediaType.parse("application/json;charset=UTF-8"), paramData))
                    .build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("intf payment response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String code = jsonNode.get("code").asText();
                if ("100000".equals(code)) {
                    TreeMap<String, String> map = objectMapper.readValue(objectMapper.writeValueAsString(jsonNode.get("data")), new TypeReference<TreeMap<String, String>>() {
                    });
                    String thirdOrderNo = map.get("mp_trade_no");
                    String payUrl = map.get("trade_url");
                    String orderNo = map.get("trade_no");
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
                    String msg = jsonNode.get("message").asText();
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
            log.error("intf payment failed!", ex);
            throw new ServerException(2001);
        }
    }

    @Override
    public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("intf payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("trade_status");
            if ("SUCCESS".equals(status)) {
                result.setPaySuccess(true);
                result.setThirdOrderNo(data.get("order_no"));
                BigDecimal tradeAmount = new BigDecimal(data.get("trade_amount"));
                tradeAmount = tradeAmount.divide(new BigDecimal(100));
                result.setRealMoney(tradeAmount);
                result.setBackThirdData("success");
            } else {
                result.setPaySuccess(false);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("intf payment signature not match: {} - {}", oldSign, checkSign);
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
            params.put("partner", payApiConfig.getMerchantId());
            params.put("sign_type", "MD5");
            params.put("trade_sn", order.getOrderNo());
            params.put("pay_amount", request.getRealNum().multiply(new BigDecimal(100)).stripTrailingZeros().toPlainString());
            params.put("wallet_code", "6220");
            params.put("country_code", "62");
            params.put("notify_url", payApiConfig.getNotifyUrl());
            params.put("bank_account", user.getName());
            params.put("bank_card_no", order.getBankCardId());
            params.put("bank_site","test");
            params.put("bank_code", order.getBankCode());
            params.put("bank_province", "test");
            params.put("bank_city", "test");
            params.put("phone", order.getMobilePhone());
            params.put("pass", "power");
            params.put("certificates_no", order.getMobilePhone());
            params.put("certificates_type", "PHONE");
            params.put("acc_type", "SAVING");
            // 签名
            String signStr = mapToQueryString(params).trim();
            // 签名
            String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()) .toLowerCase();
            params.put("sign", sign);
            String paramData = objectMapper.writeValueAsString(params);
            // 请求上游
            log.info("intf withdraw param: {}", paramData);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("intf withdraw response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String retCode = jsonNode.get("code").asText();
                if ("100000".equals(retCode)) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(false);
                    return wr;
                } else {
                    String msg = jsonNode.get("message").asText();
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
            log.error("intf withdraw failed!", ex);
            throw new ServerException(2002);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
                                                         Map<String, String> data) {
        log.info("intf withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("trade_status");
            if ("SUCCESS".equals(status)) {
                result.setState(1);
                result.setBackThirdData("SUCCESS");
            } else {
                result.setState(2);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("intf withdraw signature not match: {} - {}", oldSign, checkSign);
            result.setBackThirdData("FAIL");
        }
        return result;
    }

}
