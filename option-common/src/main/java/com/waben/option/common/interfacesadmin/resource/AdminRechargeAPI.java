package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.RechargeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "admin-core-server", contextId = "AdminRechargeAPI", qualifier = "adminRechargeAPI")
public interface AdminRechargeAPI extends BaseAPI {

    @RequestMapping(value = "/recharge/queryList", method = RequestMethod.GET)
    public Response<PageInfo<RechargeDTO>> _queryList(@RequestParam(value = "operatorId", required = false) Integer operatorId,
                                                      @RequestParam(value = "page") int page,
                                                      @RequestParam(value = "size") int size);

    public default PageInfo<RechargeDTO> queryList(Integer operatorId,int page,int size) {
        return getResponseData(_queryList(operatorId, page, size));
    }

}
