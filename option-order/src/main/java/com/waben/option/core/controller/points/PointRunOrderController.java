package com.waben.option.core.controller.points;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.request.point.PointRunRequest;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.points.PointRunOrderService;

@RestController
@RequestMapping("/point_run_order")
public class PointRunOrderController extends AbstractBaseController {

	@Resource
	private PointRunOrderService pointRunOrderService;

	@RequestMapping(value = "/user/page", method = RequestMethod.POST)
	public ResponseEntity<?> userOrderPage(@RequestBody PointRunUserOrderRequest req) {
		return ok(pointRunOrderService.userOrderPage(req));
	}

	@RequestMapping(value = "/matchMerchant", method = RequestMethod.GET)
	public ResponseEntity<?> matchMerchant(@RequestParam("userId") Long userId,
			@RequestParam("runOrderId") Long runOrderId) {
		return ok(pointRunOrderService.matchMerchant(userId, runOrderId));
	}

	@RequestMapping(value = "/matchOrder", method = RequestMethod.GET)
	public ResponseEntity<?> matchOrder(@RequestParam("userId") Long userId,
			@RequestParam("merchantId") Long merchantId) {
		return ok(pointRunOrderService.matchOrder(userId, merchantId));
	}

	@RequestMapping(value = "/run", method = RequestMethod.POST)
	public ResponseEntity<?> run(@RequestParam("userId") Long userId, @RequestBody PointRunRequest req) {
		pointRunOrderService.run(userId, req);
		return ok();
	}

	@RequestMapping(value = "/gift", method = RequestMethod.POST)
	public ResponseEntity<?> giftRunOrder(@RequestParam("userId") Long userId) {
		pointRunOrderService.giftRunOrder(userId);
		return ok();
	}

}
