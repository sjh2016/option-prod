package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.BankCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "core-server", contextId = "BankCodeAPI", qualifier = "bankCodeAPI")
public interface BankCodeAPI extends BaseAPI {

	@RequestMapping(value = "/bankCode/query", method = RequestMethod.GET)
	public Response<List<BankCodeDTO>> _query(@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "currency", required = false) String currency);

	public default List<BankCodeDTO> query(String name, String code, String currency) {
		return getResponseData(_query(name, code, currency));
	}

}
