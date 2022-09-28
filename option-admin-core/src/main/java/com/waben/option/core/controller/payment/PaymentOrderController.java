package com.waben.option.core.controller.payment;

import com.waben.option.common.model.dto.payment.PaymentOrderDTO;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.payment.*;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.payment.PaymentOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/payment_order")
public class PaymentOrderController extends AbstractBaseController {

    @Resource
    private PaymentOrderService service;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(Long id) {
        return ok(service.query(id));
    }

    @RequestMapping(value = "/last", method = RequestMethod.GET)
    public ResponseEntity<?> last(Long userId) {
        return ok(service.last(userId));
    }

    @RequestMapping(value = "/queryByOrderNo", method = RequestMethod.GET)
    public ResponseEntity<?> queryByOrderNo(String orderNo) {
        return ok(service.queryByOrderNo(orderNo));
    }

    @RequestMapping(value = "/queryByThirdOrderNo", method = RequestMethod.GET)
    public ResponseEntity<?> queryByThirdOrderNo(String thirdOrderNo) {
        return ok(service.queryByThirdOrderNo(thirdOrderNo));
    }

    @RequestMapping(value = "/hasThirdOrderNo", method = RequestMethod.GET)
    public ResponseEntity<?> hasThirdOrderNo(String thirdOrderNo) {
        return ok(service.hasThirdOrderNo(thirdOrderNo));
    }

    @RequestMapping(value = "/payOtcSuccess", method = RequestMethod.POST)
    public ResponseEntity<?> payOtcSuccess(@RequestBody PayOtcSuccessRequest req) {
        service.payOtcSuccess(req);
        return ok();
    }

    @RequestMapping(value = "/payCoinSuccess", method = RequestMethod.POST)
    public ResponseEntity<?> payCoinSuccess(@RequestBody PayCoinSuccessRequest req) {
        service.payCoinSuccess(req);
        return ok();
    }

    @RequestMapping(value = "/user/page", method = RequestMethod.POST)
    public ResponseEntity<?> userPage(@RequestParam("userId") Long userId, @RequestBody PaymentUserPageRequest req) {
        return ok(service.userPage(userId, req));
    }

    @RequestMapping(value = "/admin/page", method = RequestMethod.POST)
    public ResponseEntity<?> adminPage(@RequestBody PaymentAdminPageRequest req) {
        return ok(service.adminPage(req));
    }

    @RequestMapping(value = "/admin/sta", method = RequestMethod.POST)
    public ResponseEntity<?> adminSta(@RequestBody PaymentAdminPageRequest req) {
        return ok(service.adminSta(req));
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public ResponseEntity<?> close(@RequestBody IdRequest request) {
        service.close(request.getId());
        return ok();
    }

    @RequestMapping(value = "/createOrder", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderDTO request) {
        return ok(service.createOrder(request));
    }

    @RequestMapping(value = "/updateThirdInfo", method = RequestMethod.POST)
    public ResponseEntity<?> updateThirdInfo(@RequestBody PaymentUpdateThirdInfoRequest request) {
        service.updateThirdInfo(request);
        return ok();
    }

    @RequestMapping(value = "/inviteRechargePeopleBySymbol", method = RequestMethod.GET)
    public ResponseEntity<?> inviteRechargePeopleBySymbol(@RequestParam("symbol") String symbol) {
        return ok(service.inviteRechargePeopleBySymbol(symbol));
    }

    @RequestMapping(value = "/totalRechargeAmountByUsers", method = RequestMethod.POST)
    public ResponseEntity<?> totalRechargeAmountByUsers(@RequestBody List<Long> uidList) {
        return ok(service.totalRechargeAmountByUsers(uidList));
    }

}
