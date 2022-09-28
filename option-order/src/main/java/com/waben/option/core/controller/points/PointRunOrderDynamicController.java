package com.waben.option.core.controller.points;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.request.point.PointRunUserOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.points.PointRunOrderDynamicService;

@RestController
@RequestMapping("/point_run_order_dynamic")
public class PointRunOrderDynamicController extends AbstractBaseController {

	@Resource
	private PointRunOrderDynamicService pointRunOrderDynamicService;

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	public ResponseEntity<?> page(@RequestBody PointRunUserOrderRequest req) {
		return ok(pointRunOrderDynamicService.page(req));
	}

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public ResponseEntity<?> generate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pointRunOrderDynamicService.generate();
			}
		}).start();
		return ok();
	}

	@RequestMapping(value = "/clear", method = RequestMethod.GET)
	public ResponseEntity<?> clear() {
		pointRunOrderDynamicService.clear();
		return ok();
	}

}
