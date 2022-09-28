package com.waben.option.common.interfacesadmin.user;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.resource.ApplyUserCountDTO;
import com.waben.option.common.model.dto.resource.UserMissionCompleteDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.user.UserMissionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * @author: Peter
 * @date: 2021/7/16 18:27
 */
@FeignClient(value = "admin-core-server", contextId = "AdminUserMissionAPI", qualifier = "adminUserMissionAPI")
public interface AdminUserMissionAPI extends BaseAPI {

    @RequestMapping(value = "/mission/queryCount", method = RequestMethod.GET)
    public Response<Integer> _queryCount(@RequestParam("userId") Long userId, @RequestParam("activityType") ActivityTypeEnum activityType, @RequestParam("date") LocalDate date);

    @RequestMapping(value = "/mission/create", method = RequestMethod.POST)
    public Response<Void> _create(@RequestBody UserMissionRequest request);

    @RequestMapping(value = "/mission/awardStatus", method = RequestMethod.GET)
    public Response<List<UserMissionCompleteDTO>> _awardStatus(@RequestParam("userId") Long userId,
                                                               @RequestParam(value = "activityType", required = false) ActivityTypeEnum activityType, @RequestParam("date") LocalDate date);

    @RequestMapping(value = "/mission/award", method = RequestMethod.POST)
    public Response<Void> _award(@RequestBody UserMissionRequest request);

    @RequestMapping(value = "/mission/auto/award", method = RequestMethod.POST)
    public Response<Void> _autoAward(@RequestParam("localDate") String localDate);

    @RequestMapping(value = "/mission/queryApplyUidList", method = RequestMethod.GET)
    public Response<List<ApplyUserCountDTO>> _queryApplyUidList();

    @RequestMapping(value = "/mission/auto/applyAutoAward", method = RequestMethod.POST)
    public Response<Void> _applyAutoAward(@RequestParam("userId") Long userId);

    public default Integer queryCount(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return getResponseData(_queryCount(userId, activityType, date));
    }

    public default Void create(UserMissionRequest request) {
        return getResponseData(_create(request));
    }

    public default List<UserMissionCompleteDTO> awardStatus(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return getResponseData(_awardStatus(userId, activityType, date));
    }

    public default Void award(UserMissionRequest request) {
        return getResponseData(_award(request));
    }

    public default Void autoAward(String localDate) {
        return getResponseData(_autoAward(localDate));
    }

    public default List<ApplyUserCountDTO> queryApplyUidList() {
        return getResponseData(_queryApplyUidList());
    }

    public default void applyAutoAward(Long userId) {
        getResponseData(_applyAutoAward(userId));
    }
}
