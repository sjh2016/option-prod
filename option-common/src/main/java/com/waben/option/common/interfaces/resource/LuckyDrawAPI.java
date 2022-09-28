package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.LuckyDrawCommodityDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "core-server", contextId = "LuckyDrawAPI", qualifier = "luckyDrawAPI")
public interface LuckyDrawAPI extends BaseAPI {

    @RequestMapping(value = "/lucky/luckyDraw", method = RequestMethod.GET)
    public Response<BigDecimal> _luckyDraw(@RequestParam("userId") Long userId);

    @RequestMapping(value = "/lucky/queryLuckyDrawCommodity", method = RequestMethod.GET)
    public Response<List<LuckyDrawCommodityDTO>> _queryLuckyDrawCommodity();

    public default List<LuckyDrawCommodityDTO> queryLuckyDrawCommodity() {
        return getResponseData(_queryLuckyDrawCommodity());
    }

    public default BigDecimal luckyDraw(Long userId) {
        return getResponseData(_luckyDraw(userId));
    }

}
