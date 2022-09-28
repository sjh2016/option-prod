package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/17 1:12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_u_user_mission_complete", autoResultMap = true)
public class UserMissionComplete extends BaseEntity<Long> {

    private Long userId;

    private ActivityTypeEnum activityType;

    private Boolean status;

    private String localDate;

    private BigDecimal volume;

    private BigDecimal inviteVolume;

    private BigDecimal minLimitVolume;

    private String inviteAuditStatus;

    public static final String USER_ID = "user_id";

    public static final String INVITE_VOLUME = "invite_volume";

    public static final String STATUS = "status";

    public static final String LOCAL_DATE = "local_date";

    public static final String ACTIVITY_TYPE = "activity_type";

    public static final String INVITE_AUDIT_STATUS = "invite_audit_status";
}
