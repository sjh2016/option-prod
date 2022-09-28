package com.waben.option.common.interfaces.summary;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.summary.UserFissionDataDTO;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "UserFissionAPI", qualifier = "userFissionAPI")
public interface UserFissionAPI extends BaseAPI {

    @RequestMapping(value = "/userFission/queryList", method = RequestMethod.GET)
    public Response<PageInfo<UserFissionDataDTO>> _queryList(@RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                                             @RequestParam(value = "page", required = false) int page,
                                                             @RequestParam(value = "size", required = false) int size);

    public default PageInfo<UserFissionDataDTO> queryList(String mobilePhone,int page,int size) {
        return getResponseData(_queryList(mobilePhone,page, size));
    }

}
