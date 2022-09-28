package com.waben.option.common.interfaces.order;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.order.OrderCountDTO;
import com.waben.option.common.model.dto.order.OrderDTO;
import com.waben.option.common.model.dto.order.OrderTotalDTO;
import com.waben.option.common.model.dto.order.OrderUserStaDTO;
import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.common.model.request.order.OrderRequest;
import com.waben.option.common.model.request.order.UpdateOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "order-server", contextId = "OrderAPI", qualifier = "orderAPI", path = "/order")
public interface OrderAPI extends BaseAPI {

	@RequestMapping(value = "/place", method = RequestMethod.POST)
	public Response<String> _place(@RequestBody OrderRequest request);
	
	@RequestMapping(value = "/receiveGiveOrder", method = RequestMethod.POST)
	public Response<Void> _receiveGiveOrder(@RequestParam("userId") Long userId);

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public Response<Void> _auditOrder(@RequestBody UpdateOrderRequest request);

	@RequestMapping(value = "/orderSettlement", method = RequestMethod.GET)
	public Response<Void> _orderSettlement(@RequestParam("count") Integer count);

	@RequestMapping(value = "/settlement", method = RequestMethod.GET)
	public Response<Void> _settlement();

	@RequestMapping(value = "/queryOrderTotalByUserId", method = RequestMethod.GET)
	public Response<OrderTotalDTO> _queryOrderTotalByUserId(
			@RequestParam(value = "userId", required = false) Long userId);

	@RequestMapping(value = "/queryPage", method = RequestMethod.GET)
	public Response<PageInfo<OrderDTO>> _queryPage(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "status", required = false) OrderStatusEnum status, @RequestParam("page") int page,
			@RequestParam("size") int size,@RequestParam("topId") String topId);

	@RequestMapping(value = "/user/sta", method = RequestMethod.GET)
	public Response<OrderUserStaDTO> _userSta(@RequestParam("userId") Long userId);

	@RequestMapping(value = "/queryOrderCount", method = RequestMethod.GET)
	public Response<List<OrderCountDTO>> _queryOrderCount();

	@RequestMapping(value = "/placeRegister", method = RequestMethod.POST)
	public Response<Void> _placeRegister(@RequestBody OrderRequest request);

	@RequestMapping(value = "/userPlaceCount", method = RequestMethod.GET)
	public Response<BigDecimal> _userPlaceCount(@RequestParam(value = "userId", required = true) Long userId);

	public default Void placeRegister(OrderRequest request) {
		return getResponseData(_placeRegister(request));
	}

	public default String place(OrderRequest request) {
		return getResponseData(_place(request));
	}

	public default void auditOrder(UpdateOrderRequest request) {
		getResponseData(_auditOrder(request));
	}

	public default void orderSettlement(Integer count) {
		getResponseData(_orderSettlement(count));
	}

	public default void settlement() {
		getResponseData(_settlement());
	}

	public default OrderTotalDTO queryOrderTotalByUserId(Long userId) {
		return getResponseData(_queryOrderTotalByUserId(userId));
	}

	public default PageInfo<OrderDTO> queryPage(Long userId, OrderStatusEnum status, int page, int size, String topId) {
		return getResponseData(_queryPage(userId, status, page, size,topId));
	}
	
	public default OrderUserStaDTO userSta(Long userId) {
		return getResponseData(_userSta(userId));
	}

	public default List<OrderCountDTO> queryOrderCount() {
		return getResponseData(_queryOrderCount());
	}

	public default BigDecimal userPlaceCount(Long userId) {
		return getResponseData(_userPlaceCount(userId));
	}

	public default void receiveGiveOrder(Long userId) {
		getResponseData(_receiveGiveOrder(userId));
	}

}
