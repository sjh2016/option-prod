package com.waben.option.controller.point;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointMerchantAPI;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "OTC商家" })
@RestController
@RequestMapping("/point_merchant")
public class PointMerchantController extends AbstractBaseController {

	@Resource
	private PointMerchantAPI pointMerchantAPI;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(pointMerchantAPI.list());
	}

}
