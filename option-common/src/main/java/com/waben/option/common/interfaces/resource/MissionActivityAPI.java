package com.waben.option.common.interfaces.resource;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.PageInfo;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.MissionActivityDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.resource.MissionActivityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "core-server", contextId = "MissionActivityAPI", qualifier = "missionActivityAPI")
public interface MissionActivityAPI extends BaseAPI {

    @RequestMapping(value = "/missionActivity/queryList", method = RequestMethod.GET)
    public Response<PageInfo<MissionActivityDTO>> _queryList(@RequestParam(value = "type", required = false) String type,
                                                             @RequestParam("page") int page, @RequestParam("size") int size);

    @RequestMapping(value = "/missionActivity/create", method = RequestMethod.POST)
    public Response<MissionActivityDTO> _create(@RequestBody MissionActivityRequest request);

    @RequestMapping(value = "/missionActivity/upset", method = RequestMethod.POST)
    public Response<MissionActivityDTO> _upset(@RequestBody MissionActivityRequest request);

    @RequestMapping(value = "/missionActivity/delete", method = RequestMethod.POST)
    public Response<Void> _delete(@RequestParam("id") Long id);

    @RequestMapping(value = "/missionActivity/queryByType", method = RequestMethod.GET)
    public Response<MissionActivityDTO> _queryByType(@RequestParam("type") ActivityTypeEnum type);

    public default MissionActivityDTO queryByType(ActivityTypeEnum type) {
        return getResponseData(_queryByType(type));
    }

    public default PageInfo<MissionActivityDTO> queryList(String type, int page, int size) {
        return getResponseData(_queryList(type, page, size));
    }

    public default MissionActivityDTO create(MissionActivityRequest request) {
        return getResponseData(_create(request));
    }

    public default MissionActivityDTO upset(MissionActivityRequest request) {
        return getResponseData(_upset(request));
    }

    public default void delete(Long id) {
        getResponseData(_delete(id));
    }

}
