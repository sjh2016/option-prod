package com.waben.option.controller.point;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointProductAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "OTC产品" })
@RestController
@RequestMapping("/point_product")
public class PointProductController extends AbstractBaseController {

	@Resource
	private PointProductAPI pointProductAPI;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(pointProductAPI.list());
	}

}