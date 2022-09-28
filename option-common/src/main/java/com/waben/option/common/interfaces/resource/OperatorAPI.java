package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.OperatorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "OperatorAPI", qualifier = "operatorAPI")
public interface OperatorAPI extends BaseAPI {

    @RequestMapping(value = "/operator/queryList", method = RequestMethod.GET)
    public Response<PageInfo<OperatorDTO>> _queryList(@RequestParam(value = "operator", required = false) String operator,
                                                      @RequestParam(value = "countryId", required = false) Integer countryId,
                                                      @RequestParam(value = "page") int page,
                                                      @RequestParam(value = "size") int size);

    public default PageInfo<OperatorDTO> queryList(String operator,Integer countryId,int page,int size) {
        return getResponseData(_queryList(operator, countryId, page, size));
    }

}
