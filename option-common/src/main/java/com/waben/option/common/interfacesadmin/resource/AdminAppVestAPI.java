package com.waben.option.common.interfacesadmin.resource;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.AppVestDTO;

@FeignClient(value = "admin-core-server", contextId = "AdminAppVestAPI", qualifier = "adminAppVestAPI")
public interface AdminAppVestAPI extends BaseAPI {

	@RequestMapping(value = "/appVest/query", method = RequestMethod.GET)
	public Response<AppVestDTO> _query(@RequestParam(value = "type", required = true) Integer type,
			@RequestParam(value = "index", required = true) Integer index);

	public default AppVestDTO query(Integer type, Integer index) {
		return getResponseData(_query(type, index));
	}

}
