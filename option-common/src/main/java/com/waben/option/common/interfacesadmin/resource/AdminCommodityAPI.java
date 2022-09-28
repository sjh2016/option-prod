package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.CommodityDTO;
import com.waben.option.common.model.request.resource.CommodityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: Peter
 * @date: 2021/6/23 20:21
 */
@FeignClient(value = "admin-core-server", contextId = "AdminCommodityAPI", qualifier = "adminCommodityAPI", path = "/commodity")
public interface AdminCommodityAPI extends BaseAPI {
	
	@RequestMapping(value = "/clearUsedQuantity", method = RequestMethod.POST)
	public Response<Void> _clearUsedQuantity();

	@RequestMapping(value = "/createUpdate", method = RequestMethod.POST)
	public Response<Void> _create(@RequestBody CommodityRequest request);

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public Response<PageInfo<CommodityDTO>> _query(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam(value = "online", required = false) Boolean online);

	public default void clearUsedQuantity() {
		getResponseData(_clearUsedQuantity());
	}
	
	public default void create(CommodityRequest request) {
		getResponseData(_create(request));
	}

	public default PageInfo<CommodityDTO> queryPage(int page, int size, Boolean online) {
		return getResponseData(_query(page, size, online));
	}
}
