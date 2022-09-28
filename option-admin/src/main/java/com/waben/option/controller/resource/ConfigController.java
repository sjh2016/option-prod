package com.waben.option.controller.resource;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

@RestController
@RequestMapping("/config")
public class ConfigController extends AbstractBaseController {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Resource
	private ConfigService configService;

	@RequestMapping(value = "/queryPath", method = RequestMethod.GET)
	public ResponseEntity<?> queryPath() {
		return ok(configService.queryPath());
	}

	@RequestMapping(value = "/serverTime", method = RequestMethod.GET)
	public ResponseEntity<?> serverTime() {
		LocalDateTime now = LocalDateTime.now();
		return ok(formatter.format(now));
	}

}
