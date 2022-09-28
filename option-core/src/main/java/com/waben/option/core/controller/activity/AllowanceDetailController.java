package com.waben.option.core.controller.activity;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.AllowanceDetailService;

@RestController
@RequestMapping("/allowanceDetail")
public class AllowanceDetailController extends AbstractBaseController {

	@Resource
	private AllowanceDetailService allowanceDetailService;

	@RequestMapping(value = "/distribute", method = RequestMethod.POST)
	public ResponseEntity<?> distribute(@RequestParam("orderId") Long orderId, @RequestParam("userId") Long userId,
			@RequestParam("cycle") Integer cycle, @RequestParam("returnRate") BigDecimal returnRate,
			@RequestParam("amount") BigDecimal amount, @RequestParam("type") Integer type) {
		allowanceDetailService.distribute(orderId, userId, cycle, returnRate, amount, type);
		return ok();
	}

}
