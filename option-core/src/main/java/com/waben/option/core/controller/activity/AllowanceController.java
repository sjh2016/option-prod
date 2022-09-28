package com.waben.option.core.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.AllowanceService;

@RestController
@RequestMapping("/allowance")
public class AllowanceController extends AbstractBaseController {

	@Resource
	private AllowanceService allowanceService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query() {
		return ok(allowanceService.query());
	}

}
