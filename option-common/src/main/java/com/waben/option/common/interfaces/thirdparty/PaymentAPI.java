package com.waben.option.common.interfaces.thirdparty;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.WithdrawSystemResult;
import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "thirdparty-server", contextId = "PaymentAPI", qualifier = "paymentAPI")
public interface PaymentAPI extends BaseAPI {

    @RequestMapping(value = "/payment/pay", method = RequestMethod.POST)
    public Response<Map<String, Object>> _pay(@RequestParam("userId") Long userId,
                                              @RequestParam("userIp") String userIp, @RequestBody PayFrontRequest request);

    @RequestMapping(value = "/payment/payCallback", method = RequestMethod.POST)
    public Response<String> _payCallback(
            @RequestParam(value = "isThirdOrderNo", required = true) boolean isThirdOrderNo,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "payApiId", required = false) Long payApiId, @RequestBody Map<String, String> data);

    @RequestMapping(value = "/payment/withdraw", method = RequestMethod.POST)
    public Response<WithdrawSystemResult> _withdraw(@RequestBody WithdrawSystemRequest request);

    @RequestMapping(value = "/payment/withdrawCallback", method = RequestMethod.POST)
    public Response<String> _withdrawCallback(@RequestParam("orderNo") String orderNo,
                                              @RequestBody Map<String, String> data);

    public default Map<String, Object> pay(Long userId, String userIp, PayFrontRequest request) {
        return getResponseData(_pay(userId, userIp, request));
    }

    public default String payCallback(boolean isThirdOrderNo, String orderNo, Long payApiId, Map<String, String> data) {
        return getResponseData(_payCallback(isThirdOrderNo, orderNo, payApiId, data));
    }

    public default WithdrawSystemResult withdraw(WithdrawSystemRequest request) {
        return getResponseData(_withdraw(request));
    }

    public default String withdrawCallback(String orderNo, Map<String, String> data) {
        return getResponseData(_withdrawCallback(orderNo, data));
    }

}
