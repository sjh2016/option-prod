package com.waben.option.service.user;

import java.time.LocalDate;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.waben.option.common.interfacesadmin.user.AdminUserMissionAPI;
import com.waben.option.common.model.dto.resource.ApplyUserCountDTO;
import com.waben.option.common.model.dto.resource.UserMissionCompleteDTO;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.request.user.UserMissionRequest;

@Service
public class UserMissionService {

    @Resource
    private AdminUserMissionAPI adminUserMissionAPI;

    public List<UserMissionCompleteDTO> awardStatus(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return adminUserMissionAPI.awardStatus(userId, activityType, date);
    }

    public void award(UserMissionRequest request) {
        adminUserMissionAPI.award(request);
    }

    public Integer queryCount(Long userId, ActivityTypeEnum activityType, LocalDate date) {
        return adminUserMissionAPI.queryCount(userId, activityType, date);
    }

    public void autoAward(String localDate) {
        adminUserMissionAPI.autoAward(localDate);
    }

    public List<ApplyUserCountDTO> queryApplyUidList() {
        return adminUserMissionAPI.queryApplyUidList();
    }

    public void applyAutoAward(Long userId) {
        adminUserMissionAPI.applyAutoAward(userId);
    }

}
