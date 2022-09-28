package com.waben.option.data.entity.activity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ActivityUserJoinStatusEnum;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_activity_user_join")
public class ActivityUserJoin extends BaseEntity<Long> {

    public static final String USER_ID = "user_id";
    public static final String ACTIVITY_TYPE = "activity_type";
    public static final String DAY = "day";
    public static final String STATUS = "status";
    public static final String CURRENT_QUANTITY = "current_quantity";
    public static final String INVITE_AUDIT_STATUS = "invite_audit_status";
    public static String JOIN_USER_ID = "join_user_id";
    public static String STATEMENT_ID = "statement_id";
    /**
     * 用户ID
     */
    private Long userId;

    private Long joinUserId;

    private Long statementId;
    /**
     * 活动类型
     */
    private ActivityTypeEnum activityType;
    /**
     * 日期(yyyy-MM-dd)
     */
    private String day;
    /**
     * 状态
     */
    private ActivityUserJoinStatusEnum status;
    /**
     * 当前完成数量
     */
    private BigDecimal currentQuantity;
    /**
     * 目标数量
     * <p>
     * 可为需要完成的任务数量，也可为活动达标金额
     * </p>
     */
    private BigDecimal targetQuantity;
    /**
     * 已领取数量
     */
    private BigDecimal receiveQuantity;
    /**
     * 最近一次等待领取的时间
     */
    private LocalDateTime lastWaitingReceiveTime;
    /**
     * 领取时间
     */
    private LocalDateTime receiveTime;
    /**
     * 邀请审核状态
     */
    private InviteAuditStatusEnum inviteAuditStatus;
    /**
     * 连续签到天数
     */
    private Integer continueDays;

}
