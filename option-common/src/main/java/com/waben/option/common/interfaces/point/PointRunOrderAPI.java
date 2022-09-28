package com.waben.option.common.interfaces.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.point.PointMerchantDTO;
import com.waben.option.common.model.dto.point.PointRunOrderDTO;
import com.waben.option.common.model.request.point.PointRunRequest;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;

@FeignClient(value = "order-server", contextId = "PointRunOrderAPI", qualifier = "pointRunOrderAPI", path = "/point_run_order")
public interface PointRunOrderAPI extends BaseAPI {

	@RequestMapping(value = "/user/page", method = RequestMethod.POST)
	public Response<PageInfo<PointRunOrderDTO>> _userOrderPage(@RequestBody PointRunUserOrderRequest req);

	@RequestMapping(value = "/matchMerchant", method = RequestMethod.GET)
	public Response<PointMerchantDTO> _matchMerchant(@RequestParam("userId") Long userId,
			@RequestParam("runOrderId") Long runOrderId);

	@RequestMapping(value = "/matchOrder", method = RequestMethod.GET)
	public Response<PointRunOrderDTO> _matchOrder(@RequestParam("userId") Long userId,
			@RequestParam("merchantId") Long merchantId);

	@RequestMapping(value = "/run", method = RequestMethod.POST)
	public Response<Void> _run(@RequestParam("userId") Long userId, @RequestBody PointRunRequest req);

	@RequestMapping(value = "/gift", method = RequestMethod.POST)
	public Response<Void> _gift(@RequestParam("userId") Long userId);

	public default PageInfo<PointRunOrderDTO> userOrderPage(PointRunUserOrderRequest req) {
		return getResponseData(_userOrderPage(req));
	}

	public default PointMerchantDTO matchMerchant(Long userId, Long runOrderId) {
		return getResponseData(_matchMerchant(userId, runOrderId));
	}

	public default PointRunOrderDTO matchOrder(Long userId, Long merchantId) {
		return getResponseData(_matchOrder(userId, merchantId));
	}

	public default void run(Long userId, PointRunRequest request) {
		getResponseData(_run(userId, request));
	}

	public default void gift(Long userId) {
		getResponseData(_gift(userId));
	}

}
