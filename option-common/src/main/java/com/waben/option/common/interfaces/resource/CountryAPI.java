package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.CountryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "CountryAPI", qualifier = "countryAPI")
public interface CountryAPI extends BaseAPI {

    @RequestMapping(value = "/country/queryList", method = RequestMethod.GET)
    public Response<PageInfo<CountryDTO>> _queryList(@RequestParam(value = "country", required = false) String country,
                                                     @RequestParam(value = "page") int page,
                                                     @RequestParam(value = "size") int size);

    public default PageInfo<CountryDTO> queryList(String country,int page,int size) {
        return getResponseData(_queryList(country, page, size));
    }

}
