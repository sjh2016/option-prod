package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.ImageCodeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "ImageCodeAPI", qualifier = "imageCodeAPI")
public interface ImageCodeAPI extends BaseAPI {

    @RequestMapping(value = "/code/image", method = RequestMethod.GET)
    public Response<ImageCodeDTO> _generate(@RequestParam("n") int n);

    @RequestMapping(value = "/code/image/verify", method = RequestMethod.GET)
    public Response<Boolean> _verify(@RequestParam("sessionId") String sessionId, @RequestParam("code") String code);

    @RequestMapping(value = "/code/image/not/delete/verify", method = RequestMethod.GET)
    public Response<Boolean> _verifyNotDeleteCode(@RequestParam("sessionId") String sessionId, @RequestParam("code") String code);

    public default ImageCodeDTO generate(int n) {
        return getResponseData(_generate(n));
    }

    public default Boolean verify(String sessionId, String code) {
        return getResponseData(_verify(sessionId, code));
    }

    public default Boolean verifyNotDeleteCode(String sessionId, String code) {
        return getResponseData(_verifyNotDeleteCode(sessionId, code));
    }

}
