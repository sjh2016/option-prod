package com.waben.option.core.controller.points;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.request.point.PointMerchantRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.points.PointMerchantService;

@RestController
@RequestMapping("/point_merchant")
public class PointMerchantController extends AbstractBaseController {

	@Resource
	private PointMerchantService pointMerchantService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> list() {
		return ok(pointMerchantService.list());
	}

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ResponseEntity<?> page(int page, int size) {
		return ok(pointMerchantService.page(page, size));
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public ResponseEntity<?> create(@RequestBody PointMerchantRequest request) {
		return ok(pointMerchantService.create(request));
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> update(@RequestBody PointMerchantRequest request) {
		return ok(pointMerchantService.update(request));
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> delete(Long id) {
		pointMerchantService.delete(id);
		return ok();
	}

}
