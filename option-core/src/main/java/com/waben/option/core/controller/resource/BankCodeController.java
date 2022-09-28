package com.waben.option.core.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.BankCodeService;
import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/bankCode")
@Api(value = "轮播图接口")
public class BankCodeController extends AbstractBaseController {

	@Resource
	private BankCodeService bankCodeService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "currency", required = false) String currency) {
		return ok(bankCodeService.query(name, code, currency));
	}

}
