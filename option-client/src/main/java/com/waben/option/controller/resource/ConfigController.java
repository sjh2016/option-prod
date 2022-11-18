package com.waben.option.controller.resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;

import com.waben.option.common.interfaces.resource.ConfigAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.ConfigDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.ConfigService;

@RestController
@RequestMapping("/config")
public class ConfigController extends AbstractBaseController {

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Resource
	private ConfigService configService;

	@Resource
	private ConfigAPI configAPI;

	@RequestMapping(value = "/queryPath", method = RequestMethod.GET)
	public ResponseEntity<?> queryPath() {
		return ok(configService.queryPath());
	}

	@RequestMapping(value = "/serverTime", method = RequestMethod.GET)
	public ResponseEntity<?> serverTime() {
		LocalDateTime now = LocalDateTime.now();
		return ok(formatter.format(now));
	}

	@RequestMapping(value = "/getRate", method = RequestMethod.GET)
	public ResponseEntity<?> getRate(@RequestParam("param") String param) {
		Response<ConfigDTO> usdtRate = configAPI._queryConfig(param);
		ConfigDTO configDtoData = usdtRate.getData();
		return ok(configDtoData);
	}

}
