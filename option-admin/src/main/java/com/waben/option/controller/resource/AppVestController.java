package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.resource.AdminAppVestAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/appVest")
public class AppVestController extends AbstractBaseController {

	@Resource
	private AdminAppVestAPI adminAppVestAPI;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> query(@RequestParam(value = "type", required = true) Integer type,
			@RequestParam(value = "index", required = true) Integer index) {
		return ok(adminAppVestAPI.query(type, index));
	}

}
