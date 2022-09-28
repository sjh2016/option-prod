package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ActivityUserJoinStatusEnum;
import com.waben.option.common.model.enums.InviteAuditStatusEnum;

import lombok.Data;

/**
 * 用户活动参与信息
 */
@Data
public class ActivityUserJoinDTO {

	/** 主键ID */
	private Long id;
	/**
	 * 用户ID
	 */
	private Long userId;
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
	/**
	 * 该数据是否为昨天的签到数据(特殊处理)
	 */
	private Boolean yesterdaySignData = false;
	/**
	 * 可参与的时间间隔（分钟）
	 */
	private Long joinTimeInterval;
	/**
	 * 下一次可参与时间（时间戳，为0表示可以立即参与）
	 */
	private Long nextJoinTime;

}
