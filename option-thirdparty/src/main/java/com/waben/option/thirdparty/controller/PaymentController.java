package com.waben.option.thirdparty.controller;

import com.waben.option.common.model.request.payment.PayFrontRequest;
import com.waben.option.common.model.request.payment.WithdrawSystemRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.thirdparty.service.PaymentEntryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController extends AbstractBaseController {

    @Resource
    private PaymentEntryService paymentEntryServcie;

    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public ResponseEntity<?> pay(@RequestParam("userId") Long userId, @RequestParam("userIp") String userIp,
                                 @RequestBody PayFrontRequest request) {
        return ok(paymentEntryServcie.pay(userId, userIp, request));
    }

    @RequestMapping(value = "/payCallback", method = RequestMethod.POST)
    public ResponseEntity<?> payCallback(
            @RequestParam(value = "isThirdOrderNo", required = true) boolean isThirdOrderNo,
            @RequestParam(value = "orderNo", required = false) String orderNo,
            @RequestParam(value = "payApiId", required = false) Long payApiId, @RequestBody Map<String, String> data) {
        return ok(paymentEntryServcie.payCallback(isThirdOrderNo, orderNo, payApiId, data));
    }

    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    public ResponseEntity<?> withdraw(@RequestBody WithdrawSystemRequest request) {
        return ok(paymentEntryServcie.withdraw(request));
    }

    @RequestMapping(value = "/withdrawCallback", method = RequestMethod.POST)
    public ResponseEntity<?> withdrawCallback(@RequestParam("orderNo") String orderNo,
                                              @RequestBody Map<String, String> data) {
        return ok(paymentEntryServcie.withdrawCallback(orderNo, data));
    }

}
