package com.waben.option.common.interfacesadmin.summary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.summary.UserFissionDataDTO;

@FeignClient(value = "admin-core-server", contextId = "AdminUserFissionAPI", qualifier = "adminUserFissionAPI")
public interface AdminUserFissionAPI extends BaseAPI {

    @RequestMapping(value = "/userFission/queryList", method = RequestMethod.GET)
    public Response<PageInfo<UserFissionDataDTO>> _queryList(@RequestParam(value = "mobilePhone", required = false) String mobilePhone,
                                                             @RequestParam(value = "page", required = false) int page,
                                                             @RequestParam(value = "size", required = false) int size);

    public default PageInfo<UserFissionDataDTO> queryList(String mobilePhone,int page,int size) {
        return getResponseData(_queryList(mobilePhone,page, size));
    }

}
