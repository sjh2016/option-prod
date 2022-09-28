package com.waben.option.core.controller.account;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.model.request.account.AccountTransactionRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.account.AccountService;

/**
 * @author: Peter
 * @date: 2021/6/23 20:37
 */
@RestController
@RequestMapping("/account")
public class AccountController extends AbstractBaseController {

    @Resource
    private AccountService accountService;

    @RequestMapping(value = "/queryStatementPage", method = RequestMethod.POST)
    public ResponseEntity<?> queryStatementPage(@RequestParam(value = "userId", required = false) Long userId, @RequestBody UserAccountStatementQuery query) {
        return ok(accountService.queryAccountStatementPage(userId, query));
    }

    @RequestMapping(value = "/queryAccountList", method = RequestMethod.GET)
    public ResponseEntity<?> queryAccountList(@RequestParam(value = "uidList", required = false) List<Long> uidList, @RequestParam(value = "currency", required = false) CurrencyEnum currency) {
        return ok(accountService.queryAccountList(uidList, currency));
    }

    @RequestMapping(value = "/queryAccount", method = RequestMethod.GET)
    public ResponseEntity<?> queryAccount(@RequestParam(value = "userId", required = false) Long userId) {
        return ok(accountService.queryAccount(userId));
    }

    @RequestMapping(value = "/userWithdrawSummaryByUsers", method = RequestMethod.GET)
    public ResponseEntity<?> userWithdrawSummaryByUsers(@RequestParam("uidList") List<Long> uidList) {
        return ok(accountService.userWithdrawSummaryByUsers(uidList));
    }

    @RequestMapping(value = "/statement", method = RequestMethod.POST)
    public ResponseEntity<?> statement(@RequestBody AccountTransactionRequest request) {
        accountService.statement(request.getUserId(), request.getTransactionBeanList());
        return ok();
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.POST)
    public ResponseEntity<?> transaction(@RequestBody AccountTransactionRequest request) {
        accountService.transaction(request.getUserId(), request.getTransactionBeanList());
        return ok();
    }
}
