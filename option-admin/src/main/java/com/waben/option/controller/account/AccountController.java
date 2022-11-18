package com.waben.option.controller.account;

import java.util.List;

import javax.annotation.Resource;

import com.waben.option.common.model.query.UserAccountQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.waben.option.common.interfacesadmin.account.AdminAccountAPI;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.query.UserAccountStatementQuery;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "资金接口" })
@RestController
@RequestMapping("/account")
public class AccountController extends AbstractBaseController {

	@Resource
	private AdminAccountAPI adminAccountAPI;

	@RequestMapping(value = "/admin/queryStatementPage", method = RequestMethod.POST)
	public ResponseEntity<?> queryStatementAdminPage(@RequestBody UserAccountStatementQuery query) {
		Long userId = null;
		List<Long> uidList = query.getUidList();
		if (uidList != null && uidList.size() > 0) {
			userId = uidList.get(0);
		}
		return ok(adminAccountAPI.queryStatementPage(userId, query));
	}

	@RequestMapping(value = "/queryStatementPage", method = RequestMethod.POST)
	public ResponseEntity<?> queryStatementPage(@RequestBody UserAccountStatementQuery query) {
		query.setUidList(Lists.newArrayList(getCurrentUserId()));
		return ok(adminAccountAPI.queryStatementPage(getCurrentUserId(), query));
	}

	@RequestMapping(value = "/queryAccountList", method = RequestMethod.GET)
	public ResponseEntity<?> queryAccountList(@RequestParam(value = "uidList", required = false) List<Long> uidList,
			@RequestParam(value = "currency", required = false) CurrencyEnum currency) {
		return ok(adminAccountAPI.queryAccountList(uidList, currency));
	}

	@RequestMapping(value = "/queryAccountList/post", method = RequestMethod.POST)
	public ResponseEntity<?> queryAccountList(@RequestBody UserAccountQuery userAccountQuery) {
		return ok(adminAccountAPI.queryAccountList(userAccountQuery.getUidList(), userAccountQuery.getCurrency()));
	}

	@RequestMapping(value = "/queryAccount", method = RequestMethod.GET)
	public ResponseEntity<?> queryAccount() {
		return ok(adminAccountAPI.queryAccount(getCurrentUserId()));
	}
}
