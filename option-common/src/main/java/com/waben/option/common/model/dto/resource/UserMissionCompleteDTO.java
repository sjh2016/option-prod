package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/17 1:27
 */
@Data
public class UserMissionCompleteDTO {

    private Long userId;

    private ActivityTypeEnum activityType;

    private Boolean status;

    private String localDate;

    private BigDecimal volume;

    private BigDecimal inviteVolume;

    private BigDecimal minLimitVolume;

    private InviteAuditStatusEnum inviteAuditStatus;

}
