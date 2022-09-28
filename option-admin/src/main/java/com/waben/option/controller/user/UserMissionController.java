package com.waben.option.controller.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.option.common.interfacesadmin.activity.AdminActivityAPI;
import com.waben.option.common.model.dto.activity.ActivityUserJoinCompatibleDTO;
import com.waben.option.common.model.dto.activity.ActivityUserJoinDTO;
import com.waben.option.common.model.dto.resource.BannerDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ActivityUserJoinStatusEnum;
import com.waben.option.common.model.request.user.UserMissionRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.service.resource.MissionActivityService;
import com.waben.option.service.user.UserMissionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/userMission")
@Api(tags = {"领取奖励"})
public class UserMissionController extends AbstractBaseController {

    @Resource
    private UserMissionService userMissionService;

    @Resource
    private MissionActivityService missionActivityService;
    
    @Resource
    private AdminActivityAPI adminActivityAPI;

    @ApiOperation(value = "查看活动状态", response = BannerDTO.class)
    @RequestMapping(value = "/awardStatus", method = RequestMethod.GET)
    public ResponseEntity<?> awardStatus(@RequestParam(value = "activityType", required = false) ActivityTypeEnum activityType) {
    	List<ActivityUserJoinDTO> joinList = adminActivityAPI.joinStatusList(getCurrentUserId(), activityType);
    	List<ActivityUserJoinCompatibleDTO> result = new ArrayList<>();
    	for(ActivityUserJoinDTO join : joinList) {
    		ActivityUserJoinCompatibleDTO dto = new ActivityUserJoinCompatibleDTO();
    		dto.setUserId(join.getUserId());
    		dto.setActivityType(join.getActivityType());
    		dto.setLocalDate(join.getDay());
    		dto.setStatus(join.getStatus());
    		dto.setVolume(1);
    		if(join.getStatus() == ActivityUserJoinStatusEnum.RECEIVED) {
    			if(join.getActivityType() == ActivityTypeEnum.INVITE) {
    				dto.setInviteAuditStatus("PASS");
    				dto.setVolume(5);
    			}
    		} else {
    			if(join.getActivityType() == ActivityTypeEnum.INVITE) {
    				dto.setInviteAuditStatus("PENDING");
    				dto.setVolume(0);
    			}
    		}
    		dto.setInviteVolume(join.getCurrentQuantity().intValue());
    		dto.setMinLimitVolume(join.getTargetQuantity().intValue());
    		dto.setReceiveQuantity(join.getReceiveQuantity().intValue());
    		dto.setJoinTimeInterval(join.getJoinTimeInterval());
    		dto.setNextJoinTime(join.getNextJoinTime());
    		result.add(dto);
    	}
    	return ok(result);
        // return ok(userMissionService.awardStatus(getCurrentUserId(), activityType, LocalDate.now()));
    }

    @ApiOperation(value = "查看活动进程", response = BannerDTO.class)
    @RequestMapping(value = "/queryCount", method = RequestMethod.GET)
    public ResponseEntity<?> queryCount(@RequestParam("activityType") ActivityTypeEnum activityType) {
        return ok(userMissionService.queryCount(getCurrentUserId(), activityType, LocalDate.now()));
    }

    @ApiOperation(value = "领取奖励", response = BannerDTO.class)
    @RequestMapping(value = "/award", method = RequestMethod.POST)
    public ResponseEntity<?> award(@RequestBody UserMissionRequest request) {
        // request.setUserId(getCurrentUserId());
        // userMissionService.award(request);
    	log.info("client activity receive:" + request);
        return ok(adminActivityAPI.receive(getCurrentUserId(), request.getActivityType()));
    }

    @ApiOperation(value = "查询用户任务列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public ResponseEntity<?> queryList(@RequestParam(value = "type", required = false) String type,
                                       @ApiParam(name = "page", value = "页码") int page,
                                       @ApiParam(name = "size", value = "每页数量") int size) {
        return ok(missionActivityService.queryList(type, page, size));
    }

    @ApiOperation(value = "系统审核邀请注册奖励", response = BannerDTO.class, hidden = true)
    @RequestMapping(value = "/autoAward", method = RequestMethod.POST)
    public ResponseEntity<?> autoAward(@RequestParam("localDate") String localDate) {
        userMissionService.autoAward(localDate);
        return ok();
    }

    @ApiOperation(value = "查看邀请审核列表", response = BannerDTO.class)
    @RequestMapping(value = "/queryApplyUidList", method = RequestMethod.GET)
    public ResponseEntity<?> queryApplyUidList() {
        return ok(userMissionService.queryApplyUidList());
    }

    @ApiOperation(value = "系统审核邀请注册奖励", response = BannerDTO.class, hidden = true)
    @RequestMapping(value = "/applyAutoAward", method = RequestMethod.POST)
    public ResponseEntity<?> applyAutoAward(@RequestBody UserMissionRequest request) {
        userMissionService.applyAutoAward(request.getUserId());
        return ok();
    }

}
