package com.waben.option.common.interfacesadmin.activity;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.activity.AllowanceDTO;

@FeignClient(value = "admin-core-server", contextId = "AdminAllowanceAPI", qualifier = "adminAllowanceAPI")
public interface AdminAllowanceAPI extends BaseAPI {

	@RequestMapping(value = "/allowance/query", method = RequestMethod.GET)
	public Response<List<AllowanceDTO>> _query();

	public default List<AllowanceDTO> query() {
		return getResponseData(_query());
	}

}
