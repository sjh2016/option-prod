package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;

import com.waben.option.common.model.enums.ActivityJoinLimitEnum;
import com.waben.option.common.model.enums.ActivityTypeEnum;

import lombok.Data;

/**
 * 活动
 */
@Data
public class ActivityDTO {
	
	/** 主键ID */
	private Integer id;
	/** 活动名称 */
	private String name;
	/** 活动类型 */
	private ActivityTypeEnum type;
	/** 上传链接数量 */
	private Integer urlSize;
	/** 奖励金额 */
	private BigDecimal rewardAmount;
	/**
	 * 参与限制
	 */
	private ActivityJoinLimitEnum joinLimit;
	/**
	 * 目标数量
	 * <p>
	 * 可为需要完成的任务数量，也可为活动达标金额
	 * </p>
	 */
	private BigDecimal targetQuantity;
	/**
	 * 可领取奖励的分步骤数量
	 */
	private BigDecimal receiveStepQuantity;
	/**
	 * 可参与的时间间隔（分钟）
	 */
	private Long joinTimeInterval;
	/** 描述 */
	private String description;
	/** 是否自动审核 */
	private Boolean autoAudit;
	/** 是否生效 */
	private Boolean enable;
	/** 排序 */
	private Integer sort;

}
