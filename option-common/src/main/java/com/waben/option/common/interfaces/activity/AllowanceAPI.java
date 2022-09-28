package com.waben.option.common.interfaces.activity;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.activity.AllowanceDTO;

@FeignClient(value = "core-server", contextId = "AllowanceAPI", qualifier = "allowanceAPI")
public interface AllowanceAPI extends BaseAPI {

	@RequestMapping(value = "/allowance/query", method = RequestMethod.GET)
	public Response<List<AllowanceDTO>> _query();

	public default List<AllowanceDTO> query() {
		return getResponseData(_query());
	}

}
