package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.SunshineDTO;
import com.waben.option.common.model.enums.SunshineTypeEnum;
import com.waben.option.common.model.request.resource.SunshineRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

/**
 * @author: Peter
 * @date: 2021/7/16 18:27
 */
@FeignClient(value = "admin-core-server", contextId = "AdminRechargeAPI", qualifier = "adminRechargeAPI")
public interface AdminSunshineAPI extends BaseAPI {

	@RequestMapping(value = "/sunshine/queryPage", method = RequestMethod.GET)
	public Response<PageInfo<SunshineDTO>> _queryPage(@RequestParam(value = "userId", required = false) Long userId,
			@RequestParam(value = "enable", required = false) String enable,
			@RequestParam(value = "type", required = false) SunshineTypeEnum type,
			@RequestParam(value = "localDate", required = false) LocalDate localDate,
			@RequestParam(value = "page") int page, @RequestParam(value = "size") int size);

	@Deprecated
	@RequestMapping(value = "/sunshine/createOrUpdate", method = RequestMethod.POST)
	public Response<Void> _createOrUpdate(@RequestBody SunshineRequest request);

	@RequestMapping(value = "/sunshine/audit", method = RequestMethod.POST)
	public Response<Void> _audit(@RequestBody SunshineRequest request);

	@RequestMapping(value = "/sunshine/upload", method = RequestMethod.POST)
	public Response<Void> _upload(@RequestParam("userId") Long userId, @RequestParam("type") SunshineTypeEnum type,
			@RequestParam("url") String url);

	public default PageInfo<SunshineDTO> queryPage(Long userId, String enable, SunshineTypeEnum type,
			LocalDate localDate, int page, int size) {
		return getResponseData(_queryPage(userId, enable, type, localDate, page, size));
	}

	@Deprecated
	public default Void createOrUpdate(SunshineRequest request) {
		return getResponseData(_createOrUpdate(request));
	}

	public default void audit(SunshineRequest request) {
		getResponseData(_audit(request));
	}

	public default void upload(Long userId, SunshineTypeEnum type, String url) {
		getResponseData(_upload(userId, type, url));
	}

}
