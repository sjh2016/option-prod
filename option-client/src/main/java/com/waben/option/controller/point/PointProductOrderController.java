package com.waben.option.controller.point;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointProductOrderAPI;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "OTC产品订单" })
@RestController
@RequestMapping("/point_product_order")
public class PointProductOrderController extends AbstractBaseController {

	@Resource
	private PointProductOrderAPI pointProductOrderAPI;

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public ResponseEntity<?> userOrderList() {
		return ok(pointProductOrderAPI.userOrderList(getCurrentUserId()));
	}

	@RequestMapping(value = "/user/sta", method = RequestMethod.GET)
	public ResponseEntity<?> userSta() {
		return ok(pointProductOrderAPI.userSta(getCurrentUserId()));
	}

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public ResponseEntity<?> place(@RequestBody PointPlaceOrderRequest req) {
		pointProductOrderAPI.place(getCurrentUserId(), false, req);
		return ok();
	}

}
