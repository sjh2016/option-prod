package com.waben.option.common.interfaces.point;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.point.PointProductDTO;

@FeignClient(value = "order-server", contextId = "PointProductAPI", qualifier = "pointProductAPI", path = "/point_product")
public interface PointProductAPI extends BaseAPI {

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Response<List<PointProductDTO>> _list();

	@RequestMapping(value = "/clearSchedule", method = RequestMethod.POST)
	public Response<Void> _clearSchedule();

	public default List<PointProductDTO> list() {
		return getResponseData(_list());
	}

	public default void clearSchedule() {
		getResponseData(_clearSchedule());
	}

}
