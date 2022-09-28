package com.waben.option.common.interfacesadmin.account;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.bean.AccountTransactionBean;
import com.waben.option.common.model.dto.account.UserAccountDTO;
import com.waben.option.common.model.dto.account.UserAccountStatementDTO;
import com.waben.option.common.model.dto.summary.UserWithdrawSummaryDTO;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.model.request.account.AccountTransactionRequest;

/**
 * @author: Peter
 * @date: 2021/6/23 20:43
 */
@FeignClient(value = "admin-core-server", contextId = "AdminAccountAPI", qualifier = "adminAccountAPI", path = "/account")
public interface AdminAccountAPI extends BaseAPI {

    @RequestMapping(value = "/queryStatementPage", method = RequestMethod.POST)
    public Response<PageInfo<UserAccountStatementDTO>> _queryStatementPage(@RequestParam(value = "userId", required = false) Long userId, @RequestBody UserAccountStatementQuery query);

    @RequestMapping(value = "/queryAccountList", method = RequestMethod.GET)
    public Response<List<UserAccountDTO>> _queryAccountList(@RequestParam(value = "uidList", required = false) List<Long> uidList, @RequestParam(value = "currency", required = false) CurrencyEnum currency);

    @RequestMapping(value = "/queryAccount", method = RequestMethod.GET)
    public Response<UserAccountDTO> _queryAccount(@RequestParam(value = "userId", required = false) Long userId);

    @RequestMapping(value = "/userWithdrawSummaryByUsers", method = RequestMethod.GET)
    public Response<List<UserWithdrawSummaryDTO>> _userWithdrawSummaryByUsers(@RequestParam("uidList") List<Long> uidList);

    @RequestMapping(value = "/statement", method = RequestMethod.GET)
    public Response<Void> _statement(@RequestBody AccountTransactionRequest request);

    @RequestMapping(value = "/transaction", method = RequestMethod.GET)
    public Response<Void> _transaction(@RequestBody AccountTransactionRequest request);

    public default Void statement(Long userId, List<AccountTransactionBean> transactionBeanList) {
        return getResponseData(_statement(AccountTransactionRequest.builder()
                .userId(userId).transactionBeanList(transactionBeanList).build()));
    }

    public default Void transaction(Long userId, List<AccountTransactionBean> transactionBeanList) {
        return getResponseData(_transaction(AccountTransactionRequest.builder()
                .userId(userId).transactionBeanList(transactionBeanList).build()));
    }

    public default List<UserWithdrawSummaryDTO> userWithdrawSummaryByUsers(List<Long> uidList) {
        return getResponseData(_userWithdrawSummaryByUsers(uidList));
    }

    public default UserAccountDTO queryAccount(Long userId) {
        return getResponseData(_queryAccount(userId));
    }

    public default PageInfo<UserAccountStatementDTO> queryStatementPage(Long userId, UserAccountStatementQuery query) {
        return getResponseData(_queryStatementPage(userId, query));
    }

    public default List<UserAccountDTO> queryAccountList(List<Long> uidList, CurrencyEnum currency) {
        return getResponseData(_queryAccountList(uidList, currency));
    }
}
