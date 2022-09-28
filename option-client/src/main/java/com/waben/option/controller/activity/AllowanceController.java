package com.waben.option.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.activity.AllowanceAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/allowance")
public class AllowanceController extends AbstractBaseController {

	@Resource
	private AllowanceAPI allowanceAPI;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query() {
		return ok(allowanceAPI.query());
	}

}
