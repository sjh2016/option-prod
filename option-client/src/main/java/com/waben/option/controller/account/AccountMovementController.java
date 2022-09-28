package com.waben.option.controller.account;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.option.common.interfaces.user.AccountMovementAPI;
import com.waben.option.common.model.request.user.UserAccountMovementApplyRequest;
import com.waben.option.common.model.request.user.UserAccountMovementAuditRequest;
import com.waben.option.common.model.request.user.UserAccountMovementRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.ApiOperation;

//@Api(tags = {"上下分（实盘）"})
//@RestController
//@RequestMapping("/user/movement")
public class AccountMovementController extends AbstractBaseController {

    @Resource
    private AccountMovementAPI actualMovementService;

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public ResponseEntity<?> pagePending(UserAccountMovementRequest request) {
        return ok(actualMovementService.page(request));
    }

    /**
     * 申请上下分
     */
    @ApiOperation("申请上下分")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public ResponseEntity<?> apply(@RequestBody UserAccountMovementApplyRequest req) {
        actualMovementService.apply(req.getUserId(), req);
        return ok();
    }

    /**
     * 审核上下分
     */
    @ApiOperation("审核上下分")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public ResponseEntity<?> audit(@RequestBody UserAccountMovementAuditRequest req) {
        actualMovementService.audit(getCurrentUserId(), req);
        return ok();
    }

}
