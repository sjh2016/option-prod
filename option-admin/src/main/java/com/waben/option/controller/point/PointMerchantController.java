package com.waben.option.controller.point;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointMerchantAPI;
import com.waben.option.common.model.request.common.IdRequest;
import com.waben.option.common.model.request.point.PointMerchantRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "商家" })
@RestController
@RequestMapping("/point_merchant")
public class PointMerchantController extends AbstractBaseController {

	@Resource
	private PointMerchantAPI pointMerchantAPI;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(pointMerchantAPI.list());
	}

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ResponseEntity<?> page(int page, int size) {
		return ok(pointMerchantAPI.page(page, size));
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> create(@RequestBody PointMerchantRequest request) {
		pointMerchantAPI.create(request);
		return ok();
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody PointMerchantRequest request) {
		pointMerchantAPI.update(request);
		return ok();
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestBody IdRequest req) {
		pointMerchantAPI.delete(req.getId());
		return ok();
	}

}
