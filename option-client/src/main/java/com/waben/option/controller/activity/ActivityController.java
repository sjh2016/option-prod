package com.waben.option.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.exception.BusinessErrorConstants;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.interfaces.activity.ActivityAPI;
import com.waben.option.common.interfaces.activity.ActivityJoinRecordAPI;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;

@RestController
@RequestMapping("/activity")
public class ActivityController extends AbstractBaseController {

	@Resource
	private ActivityJoinRecordAPI aActivityJoinRecordAPI;

	@Resource
	private ActivityAPI activityAPI;

	@RequestMapping(value = "/tesla/join", method = RequestMethod.POST)
	public ResponseEntity<?> teslaJoin() {
		aActivityJoinRecordAPI.join(getCurrentUserId(), ActivityTypeEnum.TESLA_GIVE);
		throw new ServerException(BusinessErrorConstants.ERROR_ACTIVITY_JOIN_SUCCESS);
	}

	@RequestMapping(value = "/joinStatusList", method = RequestMethod.GET)
	public ResponseEntity<?> joinStatusList(
			@RequestParam(value = "activityType", required = false) ActivityTypeEnum activityType) {
		return ok(activityAPI.joinStatusList(getCurrentUserId(), activityType));
	}

	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	public ResponseEntity<?> receive(@RequestParam("activityType") ActivityTypeEnum activityType) {
		return ok(activityAPI.receive(getCurrentUserId(), activityType));
	}

}
