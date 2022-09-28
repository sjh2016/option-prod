package com.waben.option.common.interfacesadmin.resource;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.NewsDTO;

@FeignClient(value = "admin-core-server", contextId = "AdminNewsAPI", qualifier = "adminNewsAPI")
public interface AdminNewsAPI extends BaseAPI {

	@RequestMapping(value = "/news/list", method = RequestMethod.GET)
	public Response<List<NewsDTO>> _list(@RequestParam(value = "publishTime", required = false) String publishTime,
			@RequestParam(value = "size", defaultValue = "10") int size);

	@RequestMapping(value = "/news/batch/save", method = RequestMethod.POST)
	public Response<Void> _batchSave(@RequestBody List<NewsDTO> data);

	public default List<NewsDTO> list(String publishTime, int size) {
		return getResponseData(_list(publishTime, size));
	}

	public default void batchSave(List<NewsDTO> data) {
		getResponseData(_batchSave(data));
	}

}
