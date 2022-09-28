package com.waben.option.thirdparty.service.pay.redsun;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.waben.option.common.component.IdWorker;
import com.waben.option.common.configuration.StaticConfig;
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
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.util.EncryptUtil;
import com.waben.option.thirdparty.service.pay.AbstractPaymentService;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Slf4j
@Service
public class RedSunPayService extends AbstractPaymentService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private OkHttpClient okHttpClient;

    @Resource
    private PaymentOrderAPI paymentOrderAPI;
    
	@Resource
	private StaticConfig staticConfig;

    @Override
    public Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request, UserDTO user,
                                   PaymentApiConfigDTO payApiConfig, PaymentMethodDTO method, PaymentPassagewayDTO passageway) {
        log.info("user: {}, red sun payment request: {}", userId, request);
        // 创建订单
        PaymentOrderDTO order = buildFormOrder(userId, request, user, payApiConfig, method, passageway);
        TreeMap<String, Object> params = new TreeMap<>();
        params.put("appid", payApiConfig.getMerchantId());
        params.put("paytype", method.getParam());
        params.put("orderamount", request.getReqMoney().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
        params.put("ordernumber", order.getOrderNo());
        params.put("notifyurl", payApiConfig.getNotifyUrl());
        params.put("payername", user.getName() + user.getSurname());
        params.put("payeremail", user.getEmail());
        params.put("payerphonenumber", user.getMobilePhone());
        params.put("payercpf", user.getCpfCode());
        params.put("privateKey", payApiConfig.getSecretKey());
        // 签名
        String sign = EncryptUtil.getMD5(mapToQueryString(params)).toLowerCase();
        params.put("sign", sign);
        // 构建支付html
        StringBuilder builder = new StringBuilder();
        builder.append("<form id='pixForm' action='" + payApiConfig.getOrderUrl() + "' method='POST'>");
        builder.append("<input type='hidden' name='appid' value='" + payApiConfig.getMerchantId() + "'>");
        builder.append("<input type='hidden' name='paytype' value='" + method.getParam() + "'>");
        builder.append("<input type='hidden' name='orderamount' value='" + request.getReqMoney().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString() + "'>");
        builder.append("<input type='hidden' name='ordernumber' value='" + order.getOrderNo() + "'>");
        builder.append("<input type='hidden' name='notifyurl' value='" + payApiConfig.getNotifyUrl() + "'>");
        builder.append("<input type='hidden' name='payername' value='" + user.getName() + user.getSurname() + "'>");
        builder.append("<input type='hidden' name='payeremail' value='" + user.getEmail() + "'>");
        builder.append("<input type='hidden' name='payerphonenumber' value='" + user.getMobilePhone() + "'>");
        builder.append("<input type='hidden' name='payercpf' value='" + user.getCpfCode() + "'>");
        builder.append("<input type='hidden' name='privateKey' value='" + payApiConfig.getSecretKey() + "'>");
        builder.append("<input type='hidden' name='sign' value='" + sign + "'>");
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
        log.info("red sun payment callback: {}", data);
        PayCallbackHandleResult result = new PayCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(payApiConfig.getMerchantId() +
                data.get("ordernumber") + data.get("actualamount") + data.get("status") + payApiConfig.getSecretKey()).toLowerCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("status");
            if ("SUCCESS".equals(status)) {
                result.setPaySuccess(true);
                result.setRealMoney(new BigDecimal(data.get("actualamount")));
                result.setBackThirdData("SUCCESS");
            } else {
                result.setPaySuccess(false);
                result.setBackThirdData("FAIL");
            }
        } else {
            log.info("red sun payment signature not match: {} - {}", oldSign, checkSign);
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
            TreeMap<String, Object> params = new TreeMap<>();
            params.put("appid", payApiConfig.getMerchantId());
            params.put("settamount", request.getRealNum().setScale(staticConfig.getDefaultCurrency().getPrecision(), RoundingMode.DOWN).toPlainString());
            params.put("ordernumber", order.getOrderNo());
            params.put("notifyurl", payApiConfig.getNotifyUrl());
            params.put("paytype", method.getParam());
            if ("BANKCARD".equals(method.getParam())) {
                Map<String, String> baseMap = buildBRBankCodeMap();
                String bankCode = baseMap.get(order.getBankName());
                if (StringUtils.isEmpty(bankCode)) throw new ServerException(2025);
                params.put("bankcode", bankCode);
                params.put("bankname", order.getBankName());
                params.put("accountnumber", order.getBankCardId());
            } else {
                params.put("cpfcode", user.getCpfCode());
                params.put("accountnumber", order.getBurseAddress());
            }
            params.put("accountname", user.getName() + user.getSurname());
            params.put("phonenumber", user.getMobilePhone());
            params.put("privateKey", payApiConfig.getSecretKey());
            // 签名
            String sign = EncryptUtil.getMD5(mapToQueryString(params)).toLowerCase();
            params.put("sign", sign);
            // 请求上游
            String queryString = mapToQueryString(params);
            log.info("red sun withdraw param: {}", queryString);
            Request postRequest = new Request.Builder()
                    .url(payApiConfig.getOrderUrl()).post(RequestBody
                            .create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), queryString)).build();
            Response response = okHttpClient.newCall(postRequest).execute();
            String json = response.body().string();
            log.info("red sun withdraw response: " + json);
            if (response.isSuccessful()) {
                if ("SUCCESS".equals(json)) {
                    WithdrawSystemResult wr = new WithdrawSystemResult();
                    wr.setThirdRespMsg(json);
                    wr.setImmediateSuccess(false);
                    return wr;
                }
            }
            throw new ServerException(2002);
        } catch (ServerException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("red sun withdraw failed!", ex);
            throw new ServerException(2002);
        }
    }

    @Override
    public WithdrawCallbackHandleResult withdrawCallback(WithdrawOrderDTO order, PaymentApiConfigDTO payApiConfig,
                                                         Map<String, String> data) {
        log.info("red sun withdraw callback: {}", data);
        WithdrawCallbackHandleResult result = new WithdrawCallbackHandleResult();
        String oldSign = data.get("sign");
        String checkSign = EncryptUtil.getMD5(payApiConfig.getMerchantId() +
                data.get("ordernumber") + data.get("remitamount") + data.get("status") + payApiConfig.getSecretKey()).toUpperCase();
        if (oldSign.equalsIgnoreCase(checkSign)) {
            String status = data.get("status");
            if ("SUCCESS".equals(status)) {
                result.setState(1);
                result.setBackThirdData("SUCCESS");
            } else if ("FAILE".equals(status)) {
                result.setState(2);
                result.setBackThirdData("SUCCESS");
            } else {
                result.setState(3);
                result.setBackThirdData("fail");
            }
        } else {
            log.info("red sun withdraw signature not match: {} - {}", oldSign, checkSign);
            result.setBackThirdData("FAIL");
        }
        return result;
    }


    private Map<String, String> buildBRBankCodeMap() {
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("Banco do Brasil", "BDB");
        baseMap.put("Banco da Amazonia", "BDA");
        baseMap.put("Banco do Nordeste do Brasil", "BDNDB");
        baseMap.put("Banestes SA - Banco do Estado do Espírito Santo", "BSABDEDES");
        baseMap.put("Banco Santander", "BS");
        baseMap.put("Banco do Estado do Para", "BDEDP");
        baseMap.put("Banco do Estado do Rio Grande do Sul", "BDEDRGDS");
        baseMap.put("Banco do Estado de Sergipe", "BDEDS");
        baseMap.put("Banco BRB de Brasília", "BBRBDB");
        baseMap.put("Banco Intermedium S.A.", "BISA");
        baseMap.put("Unicred Norte do Paraná", "UNDP");
        baseMap.put("Cooperativa Central de Crédito Urbano Cecred", "CCDCUC");
        baseMap.put("Banco do Estado de Santa Catarina", "BDEDSC");
        baseMap.put("Cooperativa Unicred Central SP", "CUCSP");
        baseMap.put("BancoCaixaEconomicaFederal", "BCEF");
        baseMap.put("AGIPLAN", "AGIPLAN");
        baseMap.put("CRESOL Confederacao", "CC");
        baseMap.put("Cooperativa Unicred de Sete Lagoas", "CUDSL");
        baseMap.put("Banco Original S.A.", "BOSA");
        baseMap.put("Banco Bonsucesso", "BBON");
        baseMap.put("Banco Bradesco", "BBRAD");
        baseMap.put("Parana Banco", "PBAN");
        baseMap.put("Banco Nubank", "BNUB");
        baseMap.put("Banco BMG", "BBM");
        baseMap.put("Banco Itau", "BITAU");
        baseMap.put("Banco Mercantil do Brasil", "BMDB");
        baseMap.put("Banco HSBC", "BHSBC");
        baseMap.put("Banco Safra", "BSAFRA");
        baseMap.put("Banco Rendimento", "BREND");
        baseMap.put("Banco Indusval", "BINDUS");
        baseMap.put("Banco Votorantim", "BVOTO");
        baseMap.put("BANCO DAYCOVAL S.A.", "BDAYSA");
        baseMap.put("Banco Citibank", "BCITIB");
        baseMap.put("Banco Cooperativa Sicred", "BCOOPS");
        baseMap.put("Bank of America", "BOAMERICA");
        baseMap.put("Banco Cooperativa do Brasil", "BCOODB");
        baseMap.put("Picpay", "PICPAY");
        return baseMap;
    }

}
