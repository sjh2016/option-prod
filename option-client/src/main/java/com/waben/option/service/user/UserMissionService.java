package com.waben.option.service.user;

import com.waben.option.common.interfaces.user.UserMissionAPI;
import com.waben.option.common.model.dto.resource.ApplyUserCountDTO;
import com.waben.option.common.model.dto.resource.UserMissionCompleteDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.user.UserMissionRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserMissionService {

    @Resource
    private UserMissionAPI userMissionAPI;

    public List<UserMissionCompleteDTO> awardStatus(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return userMissionAPI.awardStatus(userId, activityType, date);
    }

    public void award(UserMissionRequest request) {
        userMissionAPI.award(request);
    }

    public Integer queryCount(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return userMissionAPI.queryCount(userId, activityType, date);
    }

    public void autoAward(String localDate) {
        userMissionAPI.autoAward(localDate);
    }

    public List<ApplyUserCountDTO> queryApplyUidList() {
        return userMissionAPI.queryApplyUidList();
    }

    public void applyAutoAward(Long userId) {
        userMissionAPI.applyAutoAward(userId);
    }

}
