package com.waben.option.common.interfacesadmin.summary;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.summary.FundDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "admin-core-server", contextId = "AdminFundDataAPI", qualifier = "adminFundDataAPI")
public interface AdminFundDataAPI extends BaseAPI {

    @RequestMapping(value = "/fundData/queryList", method = RequestMethod.GET)
    public Response<PageInfo<FundDataDTO>> _queryList(@RequestParam(value = "startTime", required = false) String startTime,
                                                      @RequestParam(value = "endTime", required = false) String endTime,
                                                      @RequestParam("page") int page, @RequestParam("size") int size);

    @RequestMapping(value = "/fundData/create", method = RequestMethod.POST)
    public Response<Void> _create(@RequestParam(value = "day", required = false) String day);

    public default PageInfo<FundDataDTO> queryList(String startTime,String endTime,int page,int size) {
        return getResponseData(_queryList(startTime, endTime, page, size));
    }

    public default void create(String day) {
        getResponseData(_create(day));
    }

}
