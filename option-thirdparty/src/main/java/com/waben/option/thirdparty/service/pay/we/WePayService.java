package com.waben.option.thirdparty.service.pay.we;

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
import com.waben.option.thirdparty.service.ip.IpAddressService;
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
public class WePayService extends AbstractPaymentService {

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
        log.info("user: {}, wepay payment request: {}", userId, request);
        // 银联需要选择银行
//		if ("020".equals(method.getParam()) || "021".equals(method.getParam()) || "022".equals(method.getParam())) {
//			if (StringUtils.isBlank(request.getBankCode())) {
//				throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_BANK_CODE_EMPTY);
//			}
//		}
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
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        order.setGmtCreate(now);
        paymentOrderAPI.createOrder(order);
        try {
            // 构建参数
            Map<String, Object> params = new TreeMap<>();
            params.put("version", "1.0");
            params.put("mch_id", payApiConfig.getMerchantId());
            params.put("notify_url", payApiConfig.getNotifyUrl());
            params.put("mch_order_no", String.valueOf(orderId));
            params.put("pay_type", method.getParam());
            params.put("trade_amount", request.getReqMoney().setScale(0, RoundingMode.DOWN).toPlainString());
            params.put("order_date", formatter.format(LocalDateTime.now()));
//			if ("200".equals(method.getParam()) || "220".equals(method.getParam())) {
            params.put("bank_code", request.getBankCode());
//			}
            params.put("goods_name", "recharge" + params.get("trade_amount"));
            // 签名
            String sign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign_type", "MD5");
            params.put("sign", sign);
            // 请求上游
            String paramString = mapToQueryString(params);
            log.info("wepay payment param: {}", paramString);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), paramString)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("wepay payment response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("respCode").asText();
                if ("SUCCESS".equals(status)) {
                    String thirdOrderNo = jsonNode.get("orderNo").asText();
                    String payUrl = jsonNode.get("payInfo").asText();
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
                    String msg = jsonNode.get("tradeMsg").asText();
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
            log.error("wepay payment failed!", ex);
            throw new ServerException(BusinessErrorConstants.ERROR_PAYMENT_REQUEST_FAIL);
        }
    }

    @Override
    public PayCallbackHandleResult payCallback(PaymentApiConfigDTO payApiConfig, Map<String, String> data) {
        log.info("wepay payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        // 验证签名
        String sign = data.get("sign");
        data.remove("sign");
        data.remove("signType");
        TreeMap<String, Object> checkMap = new TreeMap<>(data);
        String checkSign = EncryptUtil.getMD5(mapToQueryString(checkMap) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (sign.equalsIgnoreCase(checkSign)) {
            String status = data.get("tradeResult");
            if ("1".equals(status)) {
                result.setPaySuccess(true);
                result.setRealMoney(new BigDecimal(data.get("amount")));
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
//			if (StringUtils.isBlank(order.getBranchName())) {
//				throw new ServerException(BusinessErrorConstants.ERROR_WITHDRAW_REQ_FAIL_WITH_MSG, "IFSC支行信息不能为空");
//			}
            // 构建参数
            Map<String, Object> params = new TreeMap<>();
            params.put("mch_id", payApiConfig.getMerchantId());
            params.put("mch_transferId", order.getOrderNo());
            params.put("transfer_amount", request.getRealNum().setScale(0, RoundingMode.DOWN).toPlainString());
            params.put("apply_date", formatter.format(LocalDateTime.now()));
            params.put("bank_code", order.getBankCode());
            params.put("receive_name", order.getName());
            params.put("receive_account", order.getBankCardId());
            params.put("back_url", payApiConfig.getNotifyUrl());
            params.put("remark", cutRemark(order.getBranchName()));
            // 签名
            String sign = EncryptUtil.getMD5(mapToQueryString(params) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
            params.put("sign_type", "MD5");
            params.put("sign", sign);
            // 请求上游
            String paramString = mapToQueryString(params);
            log.info("wepay withdraw param: {}", paramString);
            Request postRequest = new Request.Builder().url(payApiConfig.getOrderUrl()).post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), paramString)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("wepay withdraw response: " + json);
            if (response.isSuccessful()) {
                JsonNode jsonNode = JacksonUtil.decodeToNode(json);
                String status = jsonNode.get("respCode").asText();
                if ("SUCCESS".equals(status)) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdOrderNo(jsonNode.get("tradeNo").asText());
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(false);
                    return wr;
                } else {
                    String msg = jsonNode.get("errorMsg").asText();
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
        log.info("wepay withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        // 验证签名
        String sign = data.get("sign");
        data.remove("sign");
        data.remove("signType");
        TreeMap<String, Object> checkMap = new TreeMap<>(data);
        String checkSign = EncryptUtil.getMD5(mapToQueryString(checkMap) + "&key=" + payApiConfig.getSecretKey()).toLowerCase();
        if (sign.equalsIgnoreCase(checkSign)) {
            String status = data.get("tradeResult");
            if ("1".equals(status)) {
                result.setState(1);
            } else if ("2".equals(status)) {
                result.setState(2);
            } else {
                result.setState(3);
            }
            result.setBackThirdData("success");
        } else {
            log.info("wepay withdraw signature not match: {} - {}", sign, checkSign);
            result.setBackThirdData("fail");
        }
        return result;
    }

	private String cutRemark(String remark){
		return StringUtils.trim(remark.replace("withdraw", ""));
	}

}
