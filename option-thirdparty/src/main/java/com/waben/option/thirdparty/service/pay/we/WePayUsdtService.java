package com.waben.option.thirdparty.service.pay.we;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.thirdparty.PaymentOrderAPI;
import com.waben.option.common.model.dto.payment.*;
import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
import com.waben.option.common.model.dto.user.UserDTO;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.PaymentUpdateThirdInfoRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.common.util.JacksonUtil;
import com.waben.option.common.util.RsaSecretUtils;
import com.waben.option.common.util.RsaUtil;
import com.waben.option.thirdparty.service.ip.IpAddressService;
import com.waben.option.thirdparty.service.pay.AbstractPaymentService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class WePayAddService extends AbstractPaymentService {

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



    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user, PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, wepay add payment request: {}", userId, request);

        if ("200".equals(method.getParam()) || "220".equals(method.getParam())) {
            request.setBankCode("BCA");
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
//        LocalDateTime now = LocalDateTime.now().plusHours(1);
//        order.setGmtCreate(now);
        paymentOrderAPI.createOrder(order);
        try {


            // 构建参数
            Map<String, Object> params = new TreeMap<>();
            params.put("mer_no",payApiConfig.getMerchantId());
            params.put("mer_order_no", String.valueOf(orderId));
            params.put("pname", "zhangsa");
            params.put("pemail", "test@mail.com");
            params.put("phone",user.getUsername());
            params.put("order_amount", request.getReqMoney().setScale(0, RoundingMode.DOWN).toPlainString());
            params.put("ccy_no","NGN");
            params.put("busi_code","100501");
            params.put("bankCode",request.getBankCode());
            params.put("notifyUrl", payApiConfig.getNotifyUrl());
            params.put("pageUrl",payApiConfig.getReturnUrl());
            params.put("goods","test");
            // 签名
            String sign = signRsA(mapToQueryString(params),payApiConfig.getPrivateKey());
            //String sign = EncryptUtil.getMD5(mapToQueryString(params)).toLowerCase();
            //params.put("sign_type", "RSA");
            params.put("sign", sign);
            // 请求上游
//            String paramString = mapToQueryString(params);
//            log.info("wepay payment param: {}", paramString);


            String jsonStr = JSON.toJSONString(params);
            log.info("wepay add payment param json: {}", jsonStr);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("wepay  add payment response: " + json);

//            String json = new RestTemplate().postForObject(url, paramString, String.class);
//            log.info("wepay payment response: " + json);
            JsonNode jsonNode = JacksonUtil.decodeToNode(json);
            if (response.isSuccessful()) {
                String status = jsonNode.get("status").asText();
                if ("SUCCESS".equals(status)) {
                    String thirdOrderNo = jsonNode.get("order_no").asText();
                    String payUrl = jsonNode.get("order_data").asText();
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
                    String msg = jsonNode.get("errMsg").asText();
                    if (!StringUtils.isBlank(msg)) {
                        throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL_WITH_MSG, new String[]{msg});
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
            log.error("wepay add payment failed!", ex);
            throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
        }
    }

    private String signRsA(String param,String priKey) throws Exception {
//        Map<String, String> retMap = RsaUtil.genKeyPair();
//        //String pubKey = retMap.get("pubKey");
//        String priKey = retMap.get("priKey");
        String priCipherText = RsaUtil.encryptByPrivate(param, priKey);
        priCipherText = URLEncoder.encode(priCipherText,"UTF-8");

//        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//        generator.initialize(1024);
//        KeyPair keyPair = generator.generateKeyPair();
//        String privateKey = Base64Utils.encodeToString(keyPair.getPrivate().getEncoded());
//        String publicKey = Base64Utils.encodeToString(keyPair.getPublic().getEncoded());
//        // 使用私钥加密
//        String encryptText = RsaSecretUtils.privateKeyEncrypt(param, privateKey);
        log.info("signRsa encryptText:{}",priCipherText);
        return priCipherText;
    }

    @Override
    public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("**************************8wepay add payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        // 验证签名
        String sign = data.get("sign");
        data.remove("sign");
        data.remove("signType");
        TreeMap<String, Object> checkMap = new TreeMap<>(data);
       //String checkSign = RsaUtil.decryptByPublic(sign, payApiConfig.getPublicKey());
        String checkSign = EncryptUtil.getMD5(mapToQueryString(checkMap) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (sign.equalsIgnoreCase(checkSign)) {
            String status = data.get("status");
            if ("SUCCESS".equalsIgnoreCase(status)) {
                result.setPaySuccess(true);
                result.setRealMoney(new BigDecimal(data.get("order_amount")));
            } else {
                result.setPaySuccess(false);
            }
            result.setBackThirdData("success");
        } else {
            log.info("wepay payment signature not match: {} - {}", sign, checkSign);
            result.setPaySuccess(false);
            result.setBackThirdData("fail");
        }
        return result;
    }

    @Override
    public WithdrawSystemResult withdraw(WithdrawSystemRequest request, UserDTO user, WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method) {
        try {
            Map<String, Object> withdrawParam = new TreeMap<>();
            withdrawParam.put("mer_no",payApiConfig.getMerchantId());
            withdrawParam.put("mer_order_no",order.getOrderNo());
            withdrawParam.put("acc_no",order.getBankCardId());
            withdrawParam.put("acc_name",order.getName());
            withdrawParam.put("ccy_no",order.getReqCurrency().name());
            withdrawParam.put("order_amount",request.getRealNum().setScale(2, RoundingMode.DOWN).toPlainString());
            withdrawParam.put("bank_code",order.getBankCode());
            withdrawParam.put("mobile_no",user.getMobilePhone());
            withdrawParam.put("notifyUrl", payApiConfig.getNotifyUrl());
            withdrawParam.put("summary", "备注");
            String sign = signRsA(mapToQueryString(withdrawParam),payApiConfig.getPrivateKey());
            withdrawParam.put("sign", sign);


            String jsonStr = JSON.toJSONString(withdrawParam);
            log.info("wepay add payment draw param json: {}", jsonStr);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).post(RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonStr)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("wepay  add payment draw response: " + json);

            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("status").asText();
                if ("SUCCESS".equals(status)) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdOrderNo(jsonNode.get("mer_order_no").asText());
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(false);
                    return wr;
                } else {
                    String msg = jsonNode.get("err_msg").asText();
                    if (!StringUtils.isBlank(msg)) {
                        throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL_WITH_MSG, "上游提示错误：" + msg);
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
            log.error("wepay withdraw failed!", ex);
            throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("wepay add withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        result.setBackThirdData(data.get("status"));
        String status = data.get("status");
        if ("SUCCESS".equalsIgnoreCase(status)){
            result.setState(1);
        }else{
            result.setState(2);
        }

        // 验证签名
//        String sign = data.get("sign");
//        data.remove("sign");
//        data.remove("signType");
//        TreeMap<String, Object> checkMap = new TreeMap<>(data);
//        String checkSign = EncryptUtil.getMD5(mapToQueryString(checkMap) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
//        if (sign.equalsIgnoreCase(checkSign)) {
//            String status = data.get("tradeResult");
//            if ("1".equals(status)) {
//                result.setState(1);
//            } else if ("2".equals(status)) {
//                result.setState(2);
//            } else {
//                result.setState(3);
//            }
//            result.setBackThirdData("success");
//        } else {
//            log.info("wepay withdraw signature not match: {} - {}", sign, checkSign);
//            result.setBackThirdData("fail");
//        }
        return result;
    }

	private String cutRemark(String remark){
		return StringUtils.trim(remark.replace("withdraw", ""));
	}

}
