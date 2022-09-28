package com.waben.option.common.interfaces.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.order.OrderDynamicDTO;

@FeignClient(value = "order-server", contextId = "OrderDynamicAPI", qualifier = "orderDynamicAPI", path = "/order_dynamic")
public interface OrderDynamicAPI extends BaseAPI {

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	public Response<PageInfo<OrderDynamicDTO>> _page(@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public Response<Void> _generate(@RequestParam("size") int size);

	public default PageInfo<OrderDynamicDTO> page(int page, int size) {
		return getResponseData(_page(page, size));
	}

	public default void generate(int size) {
		getResponseData(_generate(size));
	}

}
