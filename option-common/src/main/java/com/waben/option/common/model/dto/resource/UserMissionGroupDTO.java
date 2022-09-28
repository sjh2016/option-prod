package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

@Data
public class UserMissionGroupDTO {

    private Long userId;

    private ActivityTypeEnum activityType;

    private Integer missionCount;

    private Boolean status = false;
}
