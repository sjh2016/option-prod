package com.waben.option.core.controller.points;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.points.PointProductService;

@RestController
@RequestMapping("/point_product")
public class PointProductController extends AbstractBaseController {

	@Resource
	private PointProductService pointProductService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(pointProductService.list());
	}

	@RequestMapping(value = "/clearSchedule", method = RequestMethod.POST)
	public ResponseEntity<?> clearSchedule() {
		pointProductService.clearSchedule();
		return ok();
	}

}