package com.waben.option.common.interfaces.activity;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.enums.ActivityTypeEnum;

@FeignClient(value = "core-server", contextId = "ActivityJoinRecordAPI", qualifier = "activityJoinRecordAPI")
public interface ActivityJoinRecordAPI extends BaseAPI {

	@RequestMapping(value = "/activityJoinRecord/hasJoin", method = RequestMethod.GET)
	public Response<Boolean> _hasJoin(@RequestParam("userId") Long userId,
			@RequestParam(value = "activityType") ActivityTypeEnum activityType);

	@RequestMapping(value = "/activityJoinRecord/join", method = RequestMethod.POST)
	public Response<Void> _join(@RequestParam("userId") Long userId,
			@RequestParam(value = "activityType") ActivityTypeEnum activityType);

	public default boolean hasJoin(Long userId, ActivityTypeEnum activityType) {
		return getResponseData(_hasJoin(userId, activityType));
	}

	public default void join(Long userId, ActivityTypeEnum activityType) {
		getResponseData(_join(userId, activityType));
	}

}
