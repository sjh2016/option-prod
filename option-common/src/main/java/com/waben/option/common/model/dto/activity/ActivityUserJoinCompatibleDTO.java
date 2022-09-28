package com.waben.option.common.model.dto.activity;

import java.time.LocalDateTime;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import com.waben.option.common.model.enums.ActivityUserJoinStatusEnum;

import lombok.Data;

/**
 * 用户活动参与信息（兼容旧代码）
 */
@Data
public class ActivityUserJoinCompatibleDTO {

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
	private String localDate;
	/**
	 * 状态
	 */
	private ActivityUserJoinStatusEnum status;
	/**
	 * 数量
	 */
	private Integer volume;
	/**
	 * 当前完成数量
	 */
	private Integer inviteVolume;
	/**
	 * 目标数量
	 * <p>
	 * 可为需要完成的任务数量，也可为活动达标金额
	 * </p>
	 */
	private Integer minLimitVolume;
	/**
	 * 邀请任务状态(PENDING、PASS)
	 */
	private String inviteAuditStatus;
	/**
	 * 已领取数量
	 */
	private Integer receiveQuantity;
	/**
	 * 最近一次领取时间
	 */
	private LocalDateTime receiveTime;
	/**
	 * 可参与的时间间隔（分钟）
	 */
	private Long joinTimeInterval;
	/**
	 * 下一次可参与时间（时间戳，为0表示可以立即参与）
	 */
	private Long nextJoinTime;

}
