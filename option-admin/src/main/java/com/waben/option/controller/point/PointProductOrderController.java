package com.waben.option.controller.point;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointProductOrderAPI;
import com.waben.option.common.model.enums.ProductOrderStatusEnum;
import com.waben.option.common.model.request.point.PointAuditOrderRequest;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "OTC产品订单" })
@RestController
@RequestMapping("/point_product_order")
public class PointProductOrderController extends AbstractBaseController {

	@Resource
	private PointProductOrderAPI pointProductOrderAPI;

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public ResponseEntity<?> page(@RequestParam("userId") Long userId,
			@RequestParam("status") ProductOrderStatusEnum status, int page, int size) {
		return ok(pointProductOrderAPI.page(userId, status, page, size));
	}

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public ResponseEntity<?> placeOrder(@RequestParam("userId") Long userId,
			@RequestParam("registerGift") boolean registerGift, @RequestBody PointPlaceOrderRequest req) {
		pointProductOrderAPI.place(userId, registerGift, req);
		return ok();
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public ResponseEntity<?> auditOrder(@RequestBody PointAuditOrderRequest req) {
		pointProductOrderAPI.audit(req);
		return ok();
	}

}
