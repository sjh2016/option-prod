package com.waben.option.common.interfaces.thirdparty;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.enums.NewsTypeEnum;

@FeignClient(value = "thirdparty-server", contextId = "NewsGrabAPI", qualifier = "newsGrabAPI")
public interface NewsGrabAPI extends BaseAPI {

	@RequestMapping(value = "/news/grap", method = RequestMethod.GET)
	public Response<Void> _grap(@RequestParam("type") NewsTypeEnum type,
			@RequestParam(value = "url", required = false) String url);

	public default void grap(NewsTypeEnum type, String url) {
		getResponseData(_grap(type, url));
	}

}
