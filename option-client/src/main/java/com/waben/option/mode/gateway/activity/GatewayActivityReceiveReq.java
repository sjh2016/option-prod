package com.waben.option.mode.gateway.activity;

import com.waben.option.common.model.enums.ActivityTypeEnum;

import lombok.Data;

@Data
public class GatewayActivityReceiveReq {

	/**
	 * 活动类型
	 */
	private ActivityTypeEnum activityType;

}
