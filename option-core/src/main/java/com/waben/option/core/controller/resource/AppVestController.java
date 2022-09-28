package com.waben.option.core.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.AppVestService;

@RestController
@RequestMapping("/appVest")
public class AppVestController extends AbstractBaseController {

	@Resource
	private AppVestService appVestService;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "type", required = true) Integer type,
			@RequestParam(value = "index", required = true) Integer index) {
		return ok(appVestService.query(type, index));
	}

}
