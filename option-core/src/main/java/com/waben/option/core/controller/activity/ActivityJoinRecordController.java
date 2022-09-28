package com.waben.option.core.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.ActivityJoinRecordService;

@RestController
@RequestMapping("/activityJoinRecord")
public class ActivityJoinRecordController extends AbstractBaseController {

	@Resource
	private ActivityJoinRecordService activityJoinRecordService;

	@RequestMapping(value = "/hasJoin", method = RequestMethod.GET)
	public ResponseEntity<?> hasJoin(@RequestParam("userId") Long userId,
			@RequestParam("activityType") ActivityTypeEnum activityType) {
		return ok(activityJoinRecordService.hasJoin(userId, activityType));
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST)
	public ResponseEntity<?> join(@RequestParam("userId") Long userId,
			@RequestParam("activityType") ActivityTypeEnum activityType) {
		activityJoinRecordService.join(userId, activityType);
		return ok();
	}

}
