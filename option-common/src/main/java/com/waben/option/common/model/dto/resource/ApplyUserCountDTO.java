package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

@Data
public class ApplyUserCountDTO {

    private Long userId;

    private Integer userCount;

    private ActivityTypeEnum activityType;

    private String mobilePhone;

}
