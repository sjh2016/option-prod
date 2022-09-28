package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.payment.BindCardDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "admin-core-server", contextId = "AdminBindCardAPI", qualifier = "adminBindCardAPI")
public interface AdminBindCardAPI extends BaseAPI {

	@RequestMapping(value = "/bindcard/list", method = RequestMethod.GET)
	public Response<List<BindCardDTO>> _list(@RequestParam("userId") Long userId);

	@RequestMapping(value = "/bindcard/bind", method = RequestMethod.POST)
	public Response<Void> _bind(@RequestBody BindCardDTO request);

	@RequestMapping(value = "/bindcard/update", method = RequestMethod.POST)
	public Response<Void> _update(@RequestBody BindCardDTO request);

	@RequestMapping(value = "/bindcard/delete", method = RequestMethod.POST)
	public Response<Void> _delete(@RequestParam("id") Long id);

	public default List<BindCardDTO> list(Long userId) {
		return getResponseData(_list(userId));
	}

	public default void bind(BindCardDTO request) {
		getResponseData(_bind(request));
	}

	public default void update(BindCardDTO request) {
		getResponseData(_update(request));
	}

	public default void delete(Long id) {
		getResponseData(_delete(id));
	}

}
