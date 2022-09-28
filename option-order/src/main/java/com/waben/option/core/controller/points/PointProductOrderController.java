package com.waben.option.core.controller.points;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.enums.ProductOrderStatusEnum;
import com.waben.option.common.model.request.point.PointAuditOrderRequest;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.points.PointProductOrderService;

@RestController
@RequestMapping("/point_product_order")
public class PointProductOrderController extends AbstractBaseController {

	@Resource
	private PointProductOrderService pointProductOrderService;

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ResponseEntity<?> page(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "status", required = false) ProductOrderStatusEnum status,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return ok(pointProductOrderService.page(userId, status, page, size));
	}

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public ResponseEntity<?> userOrderList(@RequestParam("userId") Long userId) {
		return ok(pointProductOrderService.userOrderList(userId));
	}

	@RequestMapping(value = "/user/sta", method = RequestMethod.GET)
	public ResponseEntity<?> userSta(@RequestParam("userId") Long userId) {
		return ok(pointProductOrderService.userSta(userId));
	}

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public ResponseEntity<?> placeOrder(@RequestParam("userId") Long userId,
			@RequestParam("registerGift") boolean registerGift, @RequestBody PointPlaceOrderRequest req) {
		pointProductOrderService.placeOrder(userId, registerGift, req);
		return ok();
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public ResponseEntity<?> auditOrder(@RequestBody PointAuditOrderRequest req) {
		pointProductOrderService.auditOrder(req);
		return ok();
	}

	@RequestMapping(value = "/generateRunOrderSchedule", method = RequestMethod.POST)
	public ResponseEntity<?> generateRunOrderSchedule() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				pointProductOrderService.generateRunOrderSchedule();
			}
		}).start();
		return ok();
	}

}
