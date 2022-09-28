package com.waben.option.common.interfaces.point;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.point.PointRunOrderDynamicDTO;
import com.waben.option.common.model.request.point.PointRunUserOrderRequest;

@FeignClient(value = "order-server", contextId = "PointRunOrderDynamicAPI", qualifier = "pointRunOrderDynamicAPI", path = "/point_run_order_dynamic")
public interface PointRunOrderDynamicAPI extends BaseAPI {

	@RequestMapping(value = "/page", method = RequestMethod.POST)
	public Response<PageInfo<PointRunOrderDynamicDTO>> _page(@RequestBody PointRunUserOrderRequest req);

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public Response<Void> _generate();

	public default PageInfo<PointRunOrderDynamicDTO> page(PointRunUserOrderRequest req) {
		return getResponseData(_page(req));
	}

	public default void generate() {
		getResponseData(_generate());
	}

}
