package com.waben.option.core.controller.payment;

import com.waben.option.common.model.request.payment.*;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.payment.WithdrawOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/withdraw_order")
public class WithdrawOrderController extends AbstractBaseController {

    @Resource
    private WithdrawOrderService service;

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public ResponseEntity<?> query(Long id) {
        return ok(service.query(id));
    }

    @RequestMapping(value = "/queryByOrderNo", method = RequestMethod.GET)
    public ResponseEntity<?> queryByOrderNo(String orderNo) {
        return ok(service.queryByOrderNo(orderNo));
    }

    @RequestMapping(value = "/place_otc_order", method = RequestMethod.POST)
    public ResponseEntity<?> placeOtcOrder(@RequestParam("userId") Long userId,
                                           @RequestBody WithdrawOtcFrontRequest request) {
        service.placeOtcOrder(userId, request);
        return ok();
    }

    @RequestMapping(value = "/underline/successful", method = RequestMethod.POST)
    public ResponseEntity<?> underlineSuccessful(@RequestParam("auditUserId") Long auditUserId,
                                                 @RequestBody WithdrawUnderlineSuccessfulRequest request) {
        service.underlineSuccessful(auditUserId, request);
        return ok();
    }

    @RequestMapping(value = "/underline/notpass", method = RequestMethod.POST)
    public ResponseEntity<?> underlineNotpass(@RequestParam("auditUserId") Long auditUserId,
                                              @RequestBody WithdrawUnderlineNotpassRequest request) {
        service.underlineNotpass(auditUserId, request);
        return ok();
    }

    @RequestMapping(value = "/system/process", method = RequestMethod.POST)
    public ResponseEntity<?> systemProcess(@RequestParam("auditUserId") Long auditUserId,
                                           @RequestBody WithdrawSystemProcessRequest request) {
        service.systemProcess(auditUserId, request);
        return ok();
    }

    @RequestMapping(value = "/system/successful", method = RequestMethod.POST)
    public ResponseEntity<?> systemSuccessful(@RequestParam(value = "id", required = true) Long id,
                                              @RequestParam(value = "thirdOrderNo", required = false) String thirdOrderNo,
                                              @RequestParam(value = "hash", required = false) String hash) {
        service.systemSuccessful(id, thirdOrderNo, hash);
        return ok();
    }

    @RequestMapping(value = "/system/failed", method = RequestMethod.POST)
    public ResponseEntity<?> systemFailed(@RequestParam(value = "id", required = true) Long id) {
        service.systemFailed(id);
        return ok();
    }

    @RequestMapping(value = "/user/page", method = RequestMethod.POST)
    public ResponseEntity<?> userPage(@RequestParam("userId") Long userId, @RequestBody WithdrawUserPageRequest req) {
        return ok(service.userPage(userId, req));
    }

    @RequestMapping(value = "/admin/page", method = RequestMethod.POST)
    public ResponseEntity<?> adminPage(@RequestBody WithdrawAdminPageRequest req) {
        return ok(service.adminPage(req));
    }

    @RequestMapping(value = "/admin/sta", method = RequestMethod.POST)
    public ResponseEntity<?> adminSta(@RequestBody WithdrawAdminPageRequest req) {
        return ok(service.adminSta(req));
    }

    @RequestMapping(value = "/totalWithdrawAmountByUsers", method = RequestMethod.POST)
    public ResponseEntity<?> totalWithdrawAmountByUsers(@RequestBody List<Long> uidList) {
        return ok(service.totalWithdrawAmountByUsers(uidList));
    }
    
    @RequestMapping(value = "/isWithdrawTime", method = RequestMethod.GET)
    public ResponseEntity<?> isWithdrawTime() {
        return ok(service.isWithdrawTime());
    }

    @RequestMapping(value = "/draw", method = RequestMethod.POST)
    public ResponseEntity<?> draw(@RequestParam(value="topId",required = false) String topId) {
        return ok(service.draw(topId));
    }

}
