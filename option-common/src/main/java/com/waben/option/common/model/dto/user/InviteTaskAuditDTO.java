package com.waben.option.common.model.dto.user;

import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

@Data
public class InviteTaskAuditDTO {

    private Long id;

    private Long userId;

    private Integer userCount;

    private ActivityTypeEnum activityType;

    private String username;

    private String day;

    private InviteAuditStatusEnum status;

    private String realUsernme;

}
