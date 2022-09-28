package com.waben.option.core.controller.simulate;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.simulate.WithdrawalDataService;

@RestController
@RequestMapping("/withdrawalData")
public class WithdrawalDataController extends AbstractBaseController {

	@Resource
	private WithdrawalDataService service;

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public ResponseEntity<?> generate(@RequestParam("day") String day, @RequestParam("number") int number,
			@RequestParam("amountStr") String amountStr) {
		service.generateData(day, number, amountStr);
		return ok();
	}

}
