package com.waben.option.core.controller.simulate;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.simulate.UidDataService;

@RestController
@RequestMapping("/uidData")
public class UidDataController extends AbstractBaseController {

	@Resource
	private UidDataService uidDataService;

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam("number") int number) {
		uidDataService.generate(number);
		return ok();
	}

}
