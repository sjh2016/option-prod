package com.waben.option.core.controller.account;

import com.waben.option.common.model.request.user.UserAccountMovementApplyRequest;
import com.waben.option.common.model.request.user.UserAccountMovementAuditRequest;
import com.waben.option.common.model.request.user.UserAccountMovementRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.account.AccountMovementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author: Peter
 * @date: 2021/7/10 17:05
 */
@RestController
@RequestMapping("/user/movement")
public class AccountMovementController extends AbstractBaseController {


    @Resource
    private AccountMovementService accountMovementService;

    /**
     * 分页查询
     */
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public ResponseEntity<?> page(@RequestBody UserAccountMovementRequest req) {
        return ok(accountMovementService.page(req));
    }

    /**
     * 申请上下分
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    public ResponseEntity<?> apply(@RequestParam Long applyUserId, @RequestBody UserAccountMovementApplyRequest req) {
        accountMovementService.apply(applyUserId, req);
        return ok();
    }

    /**
     * 审核上下分
     */
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public ResponseEntity<?> audit(@RequestParam Long auditUserId, @RequestBody UserAccountMovementAuditRequest req) {
        accountMovementService.audit(auditUserId, req);
        return ok();
    }
}
