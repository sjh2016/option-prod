package com.waben.option.core.controller.activity;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.model.dto.activity.UpdateJoinDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.ActivityService;

@RestController
@RequestMapping("activity")
public class ActivityController extends AbstractBaseController {

	@Resource
	private ActivityService activityService;

	@RequestMapping(value = "/joinStatusList", method = RequestMethod.GET)
	public ResponseEntity<?> joinStatusList(@RequestParam("userId") Long userId,
			@RequestParam(value = "activityType", required = false) ActivityTypeEnum type) {
		return ok(activityService.joinStatusList(userId, type));
	}

	@RequestMapping(value = "/receive", method = RequestMethod.POST)
	public ResponseEntity<?> receive(@RequestParam("userId") Long userId, @RequestParam("type") ActivityTypeEnum type) {
		return ok(activityService.receive(userId, type));
	}

	@RequestMapping(value = "/updateJoin", method = RequestMethod.POST)
	public ResponseEntity<?> updateJoin(@RequestBody UpdateJoinDTO req) {
		activityService.updateJoin(req);
		return ok();
	}

	@RequestMapping(value = "/invite/receive", method = RequestMethod.POST)
	public ResponseEntity<?> inviteReceive(@RequestParam("day") String day) {
		activityService.inviteReceive(day);
		return ok();
	}

}
