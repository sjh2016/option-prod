package com.waben.option.common.interfacesadmin.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.LeaderboardDTO;
import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import com.waben.option.common.model.request.resource.LeaderboardRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: Peter
 * @date: 2021/6/23 16:46
 */
@FeignClient(value = "admin-core-server", contextId = "AdminLeaderboardAPI", qualifier = "adminLeaderboardAPI", path = "/leaderboard")
public interface AdminLeaderboardAPI extends BaseAPI {

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<Void> _create(@RequestBody LeaderboardRequest request);

    @RequestMapping(value = "/query", method = RequestMethod.GET)
    public Response<List<LeaderboardDTO>> _query(@RequestParam(value = "type", required = false) LeaderboardTypeEnum type, @RequestParam("page") int page, @RequestParam("size") int size);

    public default void create(LeaderboardRequest request) {
        getResponseData(_create(request));
    }

    public default List<LeaderboardDTO> query(LeaderboardTypeEnum type, int page, int size) {
        return getResponseData(_query(type, page, size));
    }
}
