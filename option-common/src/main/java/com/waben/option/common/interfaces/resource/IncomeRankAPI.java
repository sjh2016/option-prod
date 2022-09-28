package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.IncomeRankDTO;
import com.waben.option.common.model.request.resource.IncomeRankRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "IncomeRankAPI", qualifier = "incomeRankAPI")
public interface IncomeRankAPI extends BaseAPI {

    @RequestMapping(value = "/incomeRank/queryList", method = RequestMethod.GET)
    public Response<PageInfo<IncomeRankDTO>> _queryList(@RequestParam(value = "type", required = false) String type,@RequestParam("page") int page, @RequestParam("size") int size);

    @RequestMapping(value = "/incomeRank/create", method = RequestMethod.POST)
    public Response<IncomeRankDTO> _create(@RequestBody IncomeRankRequest request);

    @RequestMapping(value = "/incomeRank/upset", method = RequestMethod.POST)
    public Response<IncomeRankDTO> _upset(@RequestBody IncomeRankRequest request);

    @RequestMapping(value = "/incomeRank/delete", method = RequestMethod.POST)
    public Response<Void> _delete(@RequestParam("id") int id);

    public default PageInfo<IncomeRankDTO> queryList(String type,int page, int size) {
        return getResponseData(_queryList(type,page, size));
    }

    public default IncomeRankDTO create(IncomeRankRequest request) {
        return getResponseData(_create(request));
    }

    public default IncomeRankDTO upset(IncomeRankRequest request) {
        return getResponseData(_upset(request));
    }

    public default void delete(int id) {
        getResponseData(_delete(id));
    }

}
