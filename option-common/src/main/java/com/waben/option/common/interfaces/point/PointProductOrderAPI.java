package com.waben.option.common.interfaces.point;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.point.PointProductOrderDTO;
import com.waben.option.common.model.dto.point.PointProductOrderUserStaDTO;
import com.waben.option.common.model.enums.ProductOrderStatusEnum;
import com.waben.option.common.model.request.point.PointAuditOrderRequest;
import com.waben.option.common.model.request.point.PointPlaceOrderRequest;

@FeignClient(value = "order-server", contextId = "PointProductOrderAPI", qualifier = "pointProductOrderAPI", path = "/point_product_order")
public interface PointProductOrderAPI extends BaseAPI {

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public Response<PageInfo<PointProductOrderDTO>> _page(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "status", required = false) ProductOrderStatusEnum status,
			@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(value = "/user/list", method = RequestMethod.GET)
	public Response<List<PointProductOrderDTO>> _userOrderList(@RequestParam("userId") Long userId);

	@RequestMapping(value = "/user/sta", method = RequestMethod.GET)
	public Response<PointProductOrderUserStaDTO> _userSta(@RequestParam("userId") Long userId);

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public Response<Void> _place(@RequestParam("userId") Long userId,
			@RequestParam("registerGift") boolean registerGift, @RequestBody PointPlaceOrderRequest request);

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public Response<Void> _audit(@RequestBody PointAuditOrderRequest request);

	@RequestMapping(value = "/generateRunOrderSchedule", method = RequestMethod.POST)
	public Response<Void> _generateRunOrderSchedule();

	public default PageInfo<PointProductOrderDTO> page(Long userId, ProductOrderStatusEnum status, int page, int size) {
		return getResponseData(_page(userId, status, page, size));
	}

	public default List<PointProductOrderDTO> userOrderList(Long userId) {
		return getResponseData(_userOrderList(userId));
	}

	public default PointProductOrderUserStaDTO userSta(Long userId) {
		return getResponseData(_userSta(userId));
	}

	public default void place(Long userId, boolean registerGift, PointPlaceOrderRequest request) {
		getResponseData(_place(userId, registerGift, request));
	}

	public default void audit(PointAuditOrderRequest request) {
		getResponseData(_audit(request));
	}

	public default void generateRunOrderSchedule() {
		getResponseData(_generateRunOrderSchedule());
	}

}
