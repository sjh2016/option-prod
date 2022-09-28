package com.waben.option.common.interfaces.activity;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.option.common.interfaces.BaseAPI;
import com.waben.option.common.model.Response;
import com.waben.option.common.model.dto.activity.ActivityUserJoinDTO;
import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;

@FeignClient(value = "core-server", contextId = "ActivityAPI", qualifier = "activityAPI")
public interface ActivityAPI extends BaseAPI {

	@RequestMapping(value = "/activity/joinStatusList", method = RequestMethod.GET)
	public Response<List<ActivityUserJoinDTO>> _joinStatusList(@RequestParam("userId") Long userId,
			@RequestParam(value = "type", required = false) ActivityTypeEnum type);

	@RequestMapping(value = "/activity/receive", method = RequestMethod.POST)
	public Response<BigDecimal> _receive(@RequestParam("userId") Long userId, @RequestParam("type") ActivityTypeEnum type);

	@RequestMapping(value = "/activity/updateJoin", method = RequestMethod.POST)
	public Response<Void> _updateJoin(@RequestBody UpdateJoinDTO req);

	@RequestMapping(value = "/activity/invite/receive", method = RequestMethod.POST)
	public Response<Void> _inviteReceive(@RequestParam("day") String day);

	public default List<ActivityUserJoinDTO> joinStatusList(Long userId, ActivityTypeEnum activityType) {
		return getResponseData(_joinStatusList(userId, activityType));
	}

	public default BigDecimal receive(Long userId, ActivityTypeEnum type) {
		return getResponseData(_receive(userId, type));
	}

	public default void updateJoin(UpdateJoinDTO req) {
		getResponseData(_updateJoin(req));
	}

	public default void inviteReceive(String day) {
		getResponseData(_inviteReceive(day));
	}

}
