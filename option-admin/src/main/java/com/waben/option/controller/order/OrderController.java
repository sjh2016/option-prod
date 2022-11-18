package com.waben.option.controller.order;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.order.OrderAPI;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.order.UpdateOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = { "订单信息" })
@RestController
@RequestMapping("/order")
public class OrderController extends AbstractBaseController {

	@Resource
	private OrderAPI orderAPI;

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public ResponseEntity<?> place(@RequestBody OrderRequest request) {
		request.setUserId(getUserId());
		return ok(orderAPI.place(request));
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public ResponseEntity<?> auditOrder(@RequestBody UpdateOrderRequest request) {
		orderAPI.auditOrder(request);
		return ok();
	}

	@RequestMapping(value = "/queryOrderTotalByUserId", method = RequestMethod.GET)
	public ResponseEntity<?> queryOrderTotalByUserId() {
		return ok(orderAPI.queryOrderTotalByUserId(getUserId()));
	}

	@RequestMapping(value = "/admin/queryPage", method = RequestMethod.GET)
	public ResponseEntity<?> queryAdminPage(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "status", required = false) OrderStatusEnum status, @RequestParam("page") int page,
			@RequestParam("size") int size,@RequestParam(value="topId",required = false) String topId) {
		return ok(orderAPI.queryPage(userId, status, page, size,topId));
	}

	@RequestMapping(value = "/admin/queryPage/first", method = RequestMethod.GET)
	public ResponseEntity<?> queryAdminPageFirst(@RequestParam(value = "userId", required = false) Long userId,
											@RequestParam(value = "status", required = false) OrderStatusEnum status, @RequestParam("page") int page,
											@RequestParam("size") int size,@RequestParam(value="topId",required = false) String topId) {
		return ok(orderAPI.queryPageFirst(userId, status, page, size,topId));
	}

	@RequestMapping(value = "/queryPage", method = RequestMethod.GET)
	public ResponseEntity<?> queryPage(@RequestParam(value = "status", required = false) OrderStatusEnum status,
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return ok(orderAPI.queryPage(getCurrentUserId(), status, page, size,null));
	}

	@ApiOperation(value = "系统结算", response = BannerDTO.class, hidden = true)
	@RequestMapping(value = "/orderSettlement", method = RequestMethod.GET)
	public ResponseEntity<?> orderSettlement(Integer count) {
		orderAPI.orderSettlement(count);
		return ok();
	}

	@RequestMapping(value = "/queryOrderCount", method = RequestMethod.GET)
	public ResponseEntity<?> queryOrderCount() {
		return ok(orderAPI.queryOrderCount());
	}
}
