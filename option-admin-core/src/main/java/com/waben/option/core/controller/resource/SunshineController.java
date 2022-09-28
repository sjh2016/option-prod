package com.waben.option.core.controller.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.SunshineRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.resource.SunshineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author: Peter
 * @date: 2021/7/16 19:04
 */
@RestController
@RequestMapping("sunshine")
public class SunshineController extends AbstractBaseController {

	@Resource
	private SunshineService sunshineService;

	@RequestMapping(value = "/queryPage", method = RequestMethod.GET)
	public ResponseEntity<?> queryPage(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "enable", required = false) String enable,
			@RequestParam(value = "type", required = false) SunshineTypeEnum type,
			@RequestParam(value = "localDate", required = false) LocalDate localDate,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
		return ok(sunshineService.queryPage(userId, enable, type, localDate, page, size));
	}

	@Deprecated
	@RequestMapping(value = "/createOrUpdate", method = RequestMethod.POST)
	public ResponseEntity<?> createOrUpdate(@RequestBody SunshineRequest request) {
		sunshineService.createOrUpdate(request);
		return ok();
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public ResponseEntity<?> audit(@RequestBody SunshineRequest request) {
		sunshineService.audit(request);
		return ok();
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ResponseEntity<?> upload(@RequestParam("userId") Long userId, @RequestParam("type") SunshineTypeEnum type,
			@RequestParam("url") String url) {
		sunshineService.upload(userId, type, url);
		return ok();
	}

}
