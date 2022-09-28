package com.waben.option.thirdparty.service.pay;

import com.fasterxml.jackson.databind.JsonNode;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.thirdparty.service.ip.IpAddressService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PayoneinrService extends AbstractPaymentService {

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;

    @Resource
    private IdWorker idWorker;

    @Resource
    private IpAddressService ipService;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
                                   PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, dukpay payment request: {}", userId, request);
        // 判断用户手机号和邮箱地址是否存在
        if (StringUtils.isBlank(user.getName()) || StringUtils.isBlank(user.getEmail())
                || StringUtils.isBlank(user.getPostalCode())) {
            throw new ServerException(2000);
        }
        // 创建订单
        Long orderId = buildPaymentOrder(userId, request, user, payApiConfig, method, passageway);
        try {
            // 构建参数
            Map<String, Object> params = new HashMap<>();
            params.put("mch_id", payApiConfig.getMerchantId());
            params.put("out_trade_no", String.valueOf(orderId));
            params.put("total_fee", request.getReqMoney());
            params.put("pay_type", 11);
            params.put("is_raw", 1);
            // 签名
            String sign = EncryptUtil.getMD5(params.get("out_trade_no").toString() + params.get("total_fee")
                    + params.get("mch_id") + payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign", sign);
            // 请求上游
            String queryString = mapToQueryString(params);
            Request postRequest = new Request.Builder()
                    .url(payApiConfig.getOrderUrl()).post(RequestBody
                            .create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
                    .build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("dukpay payment response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                int code = jsonNode.get("status").asInt();
                if (code == 0) {
                    String thirdOrderNo = jsonNode.get("out_trade_no").asText();
                    String payUrl = jsonNode.get("pay_url").asText();
                    // 更新上游单号
                    PaymentUpdateThirdInfoRequest updateInfoReq = new PaymentUpdateThirdInfoRequest();
                    updateInfoReq.setId(orderId);
                    updateInfoReq.setThirdOrderNo(thirdOrderNo);
                    paymentOrderAPI.updateThirdInfo(updateInfoReq);
                    // 返回支付参数
                    Map<String, Object> result = new HashMap<>();
                    result.put("result_code", "0");
                    result.put("content", payUrl);
                    return result;
                } else {
                    String msg = jsonNode.get("err_msg").asText();
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
            log.error("dukpay otc payment failed!", ex);
            throw new ServerException(2001);
        }
    }

    @Override
    public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("dukpay payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        String body = data.get("body");
        JsonNode jsonNode = JacksonUtil.decodeToNode(body);
        // 验证签名
        String sign = jsonNode.get("sign").asText();
        String checkSign = EncryptUtil.getMD5(jsonNode.get("out_trade_no").asText() + jsonNode.get("total_fee").asText()
                + payApiConfig.getMerchantId() + payApiConfig.getSecretKey()).toLowerCase();
        if (sign.equalsIgnoreCase(checkSign)) {
            String status = jsonNode.get("status").asText();
            if ("0".equals(status)) {
                result.setPaySuccess(true);
                result.setThirdOrderNo(jsonNode.get("out_trade_no").asText());
                result.setRealMoney(new BigDecimal(jsonNode.get("total_fee").asText()));
            } else {
                result.setPaySuccess(false);
            }
            result.setBackThirdData("SUCCESS");
        } else {
            log.info("dukpay payment signature not match: {} - {}", sign, checkSign);
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
            Map<String, Object> params = new HashMap<>();
            params.put("mch_id", payApiConfig.getMerchantId());
            params.put("out_trade_no", order.getOrderNo());
            params.put("total_fee", order.getReqNum());
            params.put("pay_type", 86);
            params.put("body", order.getOrderNo());
            params.put("bank_account", order.getIdCard());
            params.put("account_holder", order.getName());
            params.put("deposit_bank_code", order.getBankCode());
            params.put("deposit_bank", order.getBankName());
            params.put("acct_type", "bank");
            params.put("account_holder_mobile", order.getMobilePhone());
            params.put("province", user.getAddress());
            params.put("city", user.getCity());
            params.put("sub_branch", "IFSC");
            params.put("account_holder_id", user.getEmail());
            // 签名
            String sign = EncryptUtil.getMD5(params.get("out_trade_no").toString() + params.get("total_fee")
                    + params.get("bank_account") + params.get("account_holder") + params.get("deposit_bank")
                    + params.get("account_holder_mobile") + params.get("mch_id") + payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign", sign);
            // 请求上游
            String queryString = mapToQueryString(params);
            Request postRequest = new Request.Builder()
                    .url(payApiConfig.getOrderUrl()).post(RequestBody
                            .create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString))
                    .build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("dukpay withdraw response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                int code = jsonNode.get("status").asInt();
                if (code == 0) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdOrderNo(jsonNode.get("data").get("out_trade_no").asText());
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(true);
                    return wr;
                } else {
                    String msg = jsonNode.get("msg").asText();
                    if (!StringUtils.isBlank(msg)) {
                        throw new ServerException(2002, "Erro de prompt upstream：" + msg);
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
            log.error("dukpay withdraw failed!", ex);
            throw new ServerException(2002);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("dukpay withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        String body = data.get("body");
        JsonNode jsonNode = JacksonUtil.decodeToNode(body);
        // TODO 验证签名
        String sign = "a";
        String checkSign = "a";
        if (sign.equalsIgnoreCase(checkSign)) {
            int status = jsonNode.get("status").asInt();
            if (status == 0) {
                result.setState(1);
            }
            result.setBackThirdData("SUCCESS");
        } else {
            log.info("dukpay withdraw signature not match: {} - {}", sign, checkSign);
            result.setBackThirdData("FAIL");
        }
        return result;
    }

}
