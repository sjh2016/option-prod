package com.waben.option.controller.resource;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.resource.AdminLanguageAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "语言" })
@RestController
@RequestMapping("/language")
public class LanguageController extends AbstractBaseController {

	@Resource
	private AdminLanguageAPI adminLanguageAPI;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public ResponseEntity<?> queryList() {
		return ok(adminLanguageAPI.queryLanguage());
	}

	@RequestMapping(value = "/queryByCode", method = RequestMethod.GET)
	public ResponseEntity<?> queryByCode(@RequestParam("code") String code) {
		return ok(adminLanguageAPI.queryByCode(code));
	}
}
