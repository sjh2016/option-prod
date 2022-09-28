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
import com.waben.option.common.model.dto.point.PointMerchantDTO;
import com.waben.option.common.model.request.point.PointMerchantRequest;

@FeignClient(value = "order-server", contextId = "PointMerchantAPI", qualifier = "pointMerchantAPI", path = "/point_merchant")
public interface PointMerchantAPI extends BaseAPI {

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Response<List<PointMerchantDTO>> _list();

	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public Response<PageInfo<PointMerchantDTO>> _page(@RequestParam("page") int page, @RequestParam("size") int size);

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Response<PointMerchantDTO> _create(@RequestBody PointMerchantRequest request);

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public Response<PointMerchantDTO> _update(@RequestBody PointMerchantRequest request);

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public Response<Void> _delete(@RequestParam("id") Long id);

	public default List<PointMerchantDTO> list() {
		return getResponseData(_list());
	}

	public default PageInfo<PointMerchantDTO> page(int page, int size) {
		return getResponseData(_page(page, size));
	}

	public default PointMerchantDTO create(PointMerchantRequest request) {
		return getResponseData(_create(request));
	}

	public default PointMerchantDTO update(PointMerchantRequest request) {
		return getResponseData(_update(request));
	}

	public default void delete(Long id) {
		getResponseData(_delete(id));
	}

}
