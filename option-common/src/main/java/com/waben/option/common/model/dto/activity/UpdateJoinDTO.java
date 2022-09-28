package com.waben.option.common.model.dto.activity;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateJoinDTO {
	
	/** 订单ID */
	private Long orderId;
	/** 用户ID */
	private Long userId;
	/** 活动类型 */
	private ActivityTypeEnum type;
	/** 完成数量 */
	private BigDecimal quantity;
	/**
	 * 注册用户id
	 */
	private Long joinUserId;
	
}
