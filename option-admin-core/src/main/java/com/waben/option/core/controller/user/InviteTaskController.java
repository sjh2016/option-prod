package com.waben.option.core.controller.user;

import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.request.user.InviteTaskAuditRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.activity.ActivityService;
import com.waben.option.core.service.user.UserMissionCompleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("invite/task")
public class InviteTaskController extends AbstractBaseController {

	@Resource
	private UserMissionCompleteService userMissionCompleteService;

	@Resource
	private ActivityService activityService;

	@RequestMapping(value = "/queryList", method = RequestMethod.GET)
	public ResponseEntity<?> queryAuditInviteList(
			@RequestParam(value = "status", required = false) InviteAuditStatusEnum status,
			@RequestParam(value = "day", required = false) LocalDate day,
			@RequestParam(value = "uidList", required = false) List<Long> uidList, @RequestParam("page") int page,
			@RequestParam("size") int size,
			@RequestParam("topId") String topId) {
		// return ok(userMissionCompleteService.queryList(status, day, uidList, page,
		// size));
		return ok(activityService.queryAuditInviteList(status, day, uidList, page, size,topId));
	}

	@RequestMapping(value = "/audit", method = RequestMethod.POST)
	public ResponseEntity<?> audit(@RequestBody InviteTaskAuditRequest request) {
		// userMissionCompleteService.audit(request);
		activityService.auditInvite(request);
		return ok();
	}

}
