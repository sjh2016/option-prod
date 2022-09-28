package com.waben.option.thirdparty.service.pay.flashpay;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.FlashPayUtil;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class FlashPayService extends AbstractPaymentService {

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;

    @Resource
    private StaticConfig staticConfig;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private IdWorker idWorker;

    private String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBALvAr5/Z4UuSwCKmtQKwo9cAI2vDsWpKF+4A6Uc2H+0hJKjaywvM1G4bOpY8M92FA4xVwcsJMIgckdiQOjGBFxgxSGqajqyn41CW/tZ7pwG7gZPOfdashOiuq7Sg47miTO2tRhe6/59CjJLmBRbf0HuhDhbNzJymezqG3NvfkzbRAgMBAAECgYEAtzweKouhbPgTu72m+rEZtULVTt71zx3wrL8G7BDOp8ao2IJvl1yYFb1NOEIY/CBMjjFDqLuXOb9oN0rLAHVT6wMqSU8VxzKOKxvGIKnqxeZ5yUqCPEtp6vZKenPd8fnYTPqA4KbVmmiKyocitSdyUBN8pb1O+OnU3tUaY7NN/gECQQDtQZSgQxHwiGzT9Q2dhcuz41ppWLNzzx7lrHoBj+fNNwlmKWTCEZ9m5iSyI8l2DW/mOs94psPmIMW3EfMsnHX5AkEAypXqVkKteocN/Q20kRubuGbcztxsx+knoo0bUDyEiXH0Nmf/X4+RRK/dNVUBa8gCxeKksfR6m/zLi8b3oS+dmQJABuv64hIS2+mfAzALDaVxo/XmFVy5H4/TXvABPzprkNkiNRDQF7lb5SyhtXDF1+Pcd+MPWn3DCVu7aPPlHFAUQQJBALRxj8bBoU+P2A/cE0dktfEr/eVrRKFTtW/+C6QOI2dhTsrfGwH9GMF7e6czTg64mm+0DkpLnqIuKwcXdB3oEZECQQDZPtgrYRLsBgwApydEh89N5qq9d697xR2mRU5DS0PnWwrPjOewjQJ4nm2XJQ5TAhh4XYmSIIacV+wyiAGhR7mS";

    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7wK+f2eFLksAiprUCsKPXACNrw7FqShfuAOlHNh/tISSo2ssLzNRuGzqWPDPdhQOMVcHLCTCIHJHYkDoxgRcYMUhqmo6sp+NQlv7We6cBu4GTzn3WrITorqu0oOO5okztrUYXuv+fQoyS5gUW39B7oQ4Wzcycpns6htzb35M20QIDAQAB";

    @Override
    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user, PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, flashpay payment request: {}", userId, request);
        // 判断用户手机号和邮箱地址是否存在
        if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getEmail())
                || StringUtils.isBlank(user.getMobilePhone())) {
            throw new ServerException(2000);
        }
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
            params.put("merchantNo", payApiConfig.getMerchantId());
            params.put("merchantOrderNo", String.valueOf(orderId));
            params.put("payAmount", request.getReqMoney().toPlainString());
            params.put("mobile", user.getMobilePhone());
            params.put("name", user.getName());
            params.put("method", method.getParam());
            params.put("expiryPeriod", "30");
            params.put("email", user.getEmail());
            params.put("description", "trade.create");
            params.put("notifyUrl", payApiConfig.getNotifyUrl());
            // 签名
            String sign = FlashPayUtil.encryptRequest(params, payApiConfig.getPrivateKey());
            params.put("sign", sign);
            // 请求上游
            String paramString = JacksonUtil.encode(params);
            log.info("flashpay payment param: {}", paramString);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramString)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("flashpay payment response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("status").asText();
                if ("200".equals(status)) {
                    JsonNode data = jsonNode.get("data");
                    String payUrl = data.get("paymentUrl").asText();
                    // 更新上游单号
                    PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
                    updateInfoReq.setId(orderId);
                    updateInfoReq.setThirdOrderNo(data.get("platOrderNo").asText());
                    paymentOrderAPI.updateThirdInfo(updateInfoReq);
                    // 返回支付参数
                    Map<String, Object> result = new HashMap<>();
                    result.put("type", "4");
                    result.put("content", payUrl);
                    return result;
                } else {
                    String msg = jsonNode.get("message").asText();
                    if (!StringUtils.isBlank(msg)) {
                        throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL_WITH_MSG,
                                new String[]{msg});
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
        log.info("flashpay payment callback: {}", JSON.toJSONString(data));
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        if (FlashPayUtil.decryptRequest(data, payApiConfig.getPublicKey())) {
            String orderStatus = data.get("orderStatus");
            if ("SUCCESS".equals(orderStatus)) {
                result.setPaySuccess(true);
                result.setThirdOrderNo(data.get("platOrderNo"));
                result.setRealMoney(new BigDecimal(data.get("factAmount")));
                result.setBackThirdData("success");
            } else {
                result.setPaySuccess(false);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("fuyou payment signature not match: {}", data.get("sign"));
            result.setPaySuccess(false);
            result.setBackThirdData("fail");
        }
        return result;
    }

    @Override
    public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method) {
        try {
            // 构建参数
            TreeMap<String, Object> params = new TreeMap<>();
            params.put("merchantNo", payApiConfig.getMerchantId());
            params.put("merchantOrderNo", order.getOrderNo());
            params.put("payAmount", request.getRealNum()
                    .setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
            params.put("description", order.getCashType());
            params.put("bankCode", order.getBankCode());
            params.put("mobile", order.getMobilePhone());
            params.put("bankNumber", order.getBankCardId());
            params.put("accountHoldName", order.getName());
            params.put("notifyUrl", payApiConfig.getNotifyUrl());
            // 签名
            String s = mapToQueryString(params);
            // 签名
            String sign = FlashPayUtil.encryptRequest(params, payApiConfig.getPrivateKey());
            params.put("sign", sign);
            // 请求上游
            String paramData = objectMapper.writeValueAsString(params);
            log.info("flashpay withdraw param: {}", paramData);
            Request postRequest = new Request.Builder()
                    .url(payApiConfig.getOrderUrl()).post(RequestBody
                            .create(MediaType.parse("application/json;charset=UTF-8"), paramData))
                    .build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("flashpay withdraw response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("status").asText();
                if ("200".equals(status)) {
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
            log.error("fuyou withdraw failed!", ex);
            throw new ServerException(2002);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("flashpay withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        if (FlashPayUtil.decryptRequest(data, payApiConfig.getPublicKey())) {
            String orderStatus = data.get("orderStatus");
            if ("SUCCESS".equals(orderStatus)) {
                result.setState(1);
                result.setThirdOrderNo(data.get("platOrderNo"));
            } else if ("FAILED".equals(orderStatus)) {
                result.setState(2);
            } else {
                result.setState(3);
            }
            result.setBackThirdData("ok");
        } else {
            log.info("flashpay withdraw signature not match: {}", data.get("sign"));
            result.setBackThirdData("FAIL");
        }
        return result;
    }

    @Override
    public String mapToValueString(Map<String, Object> map) {
        return super.mapToValueString(map);
    }

}
