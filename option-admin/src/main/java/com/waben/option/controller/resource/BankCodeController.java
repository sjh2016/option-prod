package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.configuration.StaticConfig;
import com.waben.option.common.interfacesadmin.resource.AdminBankCodeAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/bankCode")
@Api(tags = { "获取银行编码" })
public class BankCodeController extends AbstractBaseController {

	@Resource
	private AdminBankCodeAPI adminBankCodeAPI;
	
	@Resource
	private StaticConfig staticConfig;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "code", required = false) String code) {
		return ok(adminBankCodeAPI.query(name, code, staticConfig.getDefaultCurrency().name()));
	}

}
