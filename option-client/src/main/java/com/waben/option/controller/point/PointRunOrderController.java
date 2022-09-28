package com.waben.option.controller.point;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfaces.point.PointRunOrderAPI;
import com.waben.option.common.interfaces.point.PointRunOrderDynamicAPI;
import com.waben.option.common.model.enums.RunOrderStatusEnum;
import com.waben.option.common.model.request.point.PointRunRequest;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;
import com.waben.option.common.web.controller.AbstractBaseController;

import io.swagger.annotations.Api;

@Api(tags = { "OTC兑换订单" })
@RestController
@RequestMapping("/point_run_order")
public class PointRunOrderController extends AbstractBaseController {

	@Resource
	private PointRunOrderAPI pointRunOrderAPI;

	@Resource
	private PointRunOrderDynamicAPI pointRunOrderDynamicAPI;

	@RequestMapping(value = "/pending/page", method = RequestMethod.GET)
	public ResponseEntity<?> pendingPage(int page, int size) {
		PointRunUserOrderRequest req = new PointRunUserOrderRequest();
		req.setPage(page);
		req.setSize(size);
		req.setUserId(getCurrentUserId());
		List<RunOrderStatusEnum> statusList = new ArrayList<>();
		statusList.add(RunOrderStatusEnum.PENDING);
		req.setStatusList(statusList);
		return ok(pointRunOrderAPI.userOrderPage(req));
	}

	@RequestMapping(value = "/used/page", method = RequestMethod.GET)
	public ResponseEntity<?> usedPage(int page, int size) {
		PointRunUserOrderRequest req = new PointRunUserOrderRequest();
		req.setPage(page);
		req.setSize(size);
		req.setUserId(getCurrentUserId());
		List<RunOrderStatusEnum> statusList = new ArrayList<>();
		statusList.add(RunOrderStatusEnum.PROCESSING);
		statusList.add(RunOrderStatusEnum.SUCCESSFUL);
		req.setStatusList(statusList);
		return ok(pointRunOrderAPI.userOrderPage(req));
	}

	@RequestMapping(value = "/dynamic/page", method = RequestMethod.GET)
	public ResponseEntity<?> dynamicPage(int page, int size) {
		PointRunUserOrderRequest req = new PointRunUserOrderRequest();
		req.setPage(page);
		req.setSize(size);
		List<RunOrderStatusEnum> statusList = new ArrayList<>();
		req.setStatusList(statusList);
		return ok(pointRunOrderDynamicAPI.page(req));
	}

	@RequestMapping(value = "/matchMerchant", method = RequestMethod.GET)
	public ResponseEntity<?> matchMerchant(@RequestParam("runOrderId") Long runOrderId) {
		return ok(pointRunOrderAPI.matchMerchant(getCurrentUserId(), runOrderId));
	}

	@RequestMapping(value = "/matchOrder", method = RequestMethod.GET)
	public ResponseEntity<?> matchOrder(@RequestParam("merchantId") Long merchantId) {
		return ok(pointRunOrderAPI.matchOrder(getCurrentUserId(), merchantId));
	}

	@RequestMapping(value = "/run", method = RequestMethod.POST)
	public ResponseEntity<?> run(@RequestBody PointRunRequest req) {
		pointRunOrderAPI.run(getCurrentUserId(), req);
		return ok();
	}

}
