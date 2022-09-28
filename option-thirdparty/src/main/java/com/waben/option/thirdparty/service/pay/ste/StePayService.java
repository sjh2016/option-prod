package com.waben.option.thirdparty.service.pay.ste;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.PayCallbackHandleResult;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO;
import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
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

/**
 * @author: Peter
 * @date: 2021/7/6 13:33
 */
@Slf4j
@Service
public class StePayService extends AbstractPaymentService {

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
                                   PaymentApiConfigDTO payApiConfig, PaymentApiConfigDTO.PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, ste payment request: {}", userId, request);
        userIp = "127.0.0.1";
        // 创建订单
        PaymentOrderDTO order = buildFormOrder(userId, request, user, payApiConfig, method, passageway);
        // 构建参数
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("mch_id", payApiConfig.getMerchantId());
        params.put("mch_order_no", order.getOrderNo());
        params.put("pay_type", method.getParam());
        params.put("trade_amount", request.getReqMoney().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
        params.put("notify_url", payApiConfig.getNotifyUrl());
        params.put("goods_name", "solar power");
        params.put("currency", "IDR");
        params.put("payer_ip", userIp);
        params.put("order_date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("page_url", payApiConfig.getReturnUrl());
        // 签名
        String signStr = mapToQueryString(params);
        log.info("ste payment sign str :{}", signStr);
        String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        // 构建支付html
        StringBuilder builder = new StringBuilder();
        builder.append("<form id='pixForm' action='" + payApiConfig.getOrderUrl() + "' method='POST'>");
        builder.append("<input type='hidden' name='mch_id' value='" + payApiConfig.getMerchantId() + "'>");
        builder.append("<input type='hidden' name='mch_order_no' value='" + order.getOrderNo() + "'>");
        builder.append("<input type='hidden' name='pay_type' value='" + method.getParam() + "'>");
        builder.append("<input type='hidden' name='trade_amount' value='" + request.getReqMoney().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString() + "'>");
        builder.append("<input type='hidden' name='notify_url' value='" + payApiConfig.getNotifyUrl() + "'>");
        builder.append("<input type='hidden' name='goods_name' value='solar power'>");
        builder.append("<input type='hidden' name='currency' value='IDR'>");
        builder.append("<input type='hidden' name='payer_ip' value='" + userIp + "'>");
        builder.append("<input type='hidden' name='order_date' value='" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'>");
        builder.append("<input type='hidden' name='page_url' value='" + payApiConfig.getReturnUrl() + "'>");
        builder.append("<input type='hidden' name='sign' value='" + sign + "'>");
        builder.append("<input type='hidden' name='sing_type' value='MD5'>");
        builder.append("</form>");
        order.setPayHtml(builder.toString());
        paymentOrderAPI.createOrder(order);
        // 返回支付参数
        Map<String, Object> result = new HashMap<>();
        result.put("type", "5");
        result.put("content", builder.toString());
        return result;
    }

    @Override
    public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("ste payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("tradeResult");
            if ("1".equals(status)) {
                result.setPaySuccess(true);
                result.setThirdOrderNo(data.get("mchOrderNo"));
                result.setRealMoney(new BigDecimal(data.get("amount")));
                result.setBackThirdData("success");
            } else {
                result.setPaySuccess(false);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("ste payment signature not match: {} - {}", oldSign, checkSign);
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
            params.put("mch_order_no", order.getOrderNo());
            params.put("pay_type", method.getParam());
            params.put("notify_url", payApiConfig.getNotifyUrl());
            params.put("trade_amount", request.getRealNum().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
            params.put("currency", "IDR");
            params.put("name", user.getName() + user.getSurname());
            params.put("account_digit", "1");
            params.put("document_id", user.getCpfCode());
            params.put("bankcode", "PHONE");
            params.put("account_number", user.getMobilePhone());
            String signStr = mapToQueryString(params);
            log.info("ste withdraw sign str :{}", signStr);
            String sign = EncryptUtil.getMD5(signStr + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign", sign);
            // 请求上游
            String paramData = objectMapper.writeValueAsString(params);
            log.info("ste withdraw param: {} ", paramData);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl())
                    .post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), paramData)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("ste withdraw response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String code = jsonNode.get("code").asText();
                if ("0".equals(code)) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(false);
                    return wr;
                }
                String msg = jsonNode.get("message").asText();
                throw new ServerException(2002, "上游提示错误：" + msg);
            }
            throw new ServerException(2002);
        } catch (ServerException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("ste withdraw failed!", ex);
            throw new ServerException(2002);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
                                                         Map<String, String> data) {
        log.info("ste withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(mapToString(new TreeMap<>(data)) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("tradeResult");
            if ("1".equals(status)) {
                result.setState(1);
                result.setBackThirdData("success");
            } else if ("2".equals(status)) {
                result.setState(2);
                result.setBackThirdData("success");
            } else {
                result.setState(3);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("ste withdraw signature not match: {} - {}", oldSign, checkSign);
            result.setBackThirdData("fail");
        }
        return result;
    }
}
