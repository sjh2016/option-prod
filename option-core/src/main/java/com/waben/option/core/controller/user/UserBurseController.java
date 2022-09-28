package com.waben.option.core.controller.user;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.dto.user.UserBurseDTO;
import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.user.UserBurseService;

@RestController
@RequestMapping("/user_burse")
public class UserBurseController extends AbstractBaseController {

	@Resource
	private UserBurseService userBurseService;

	@RequestMapping(method = RequestMethod.GET, value = "/query")
	public ResponseEntity<?> query(@RequestParam("userId") Long userId, @RequestParam("currency") CurrencyEnum currency,
			@RequestParam("burseType") BurseTypeEnum burseType, @RequestParam("payApiId") Long payApiId) {
		return ok(userBurseService.query(userId, currency, burseType, payApiId));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/queryByAddress")
	public ResponseEntity<?> queryByAddress(@RequestParam("address") String address) {
		return ok(userBurseService.queryByAddress(address));
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create")
	public ResponseEntity<?> create(@RequestBody UserBurseDTO req) {
		userBurseService.create(req);
		return ok();
	}

}
