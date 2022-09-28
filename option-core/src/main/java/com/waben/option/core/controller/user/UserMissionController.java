package com.waben.option.core.controller.user;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.user.UserMissionRequest;
import com.waben.option.common.web.controller.AbstractBaseController;
import com.waben.option.core.service.user.UserMissionCompleteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author: Peter
 * @date: 2021/7/16 19:04
 */
@RestController
@RequestMapping("mission")
public class UserMissionController extends AbstractBaseController {

    @Resource
    private UserMissionCompleteService userMissionCompleteService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestBody UserMissionRequest request) {
        userMissionCompleteService.create(request);
        return ok();
    }

    @RequestMapping(value = "/awardStatus", method = RequestMethod.GET)
    public ResponseEntity<?> awardStatus(@RequestParam("userId") Long userId,
                                         @RequestParam(value = "activityType", required = false) ActivityTypeEnum activityType, @RequestParam("date") LocalDate date) {
        return ok(userMissionCompleteService.awardStatus(userId, activityType, date));
    }

    @RequestMapping(value = "/award", method = RequestMethod.POST)
    public ResponseEntity<?> award(@RequestBody UserMissionRequest request) {
        userMissionCompleteService.award(request);
        return ok();
    }

    @RequestMapping(value = "/auto/award", method = RequestMethod.POST)
    public ResponseEntity<?> autoAward(@RequestParam("localDate") String localDate) {
        userMissionCompleteService.autoAward(ActivityTypeEnum.INVITE, localDate);
        return ok();
    }


}
