package com.waben.option.mode.gateway.activity;

import com.waben.option.common.model.enums.SunshineTypeEnum;

import lombok.Data;

@Data
public class GatewaySunshineSubmitReq {

	/**
	 * 类型
	 */
	private SunshineTypeEnum type;
	/**
	 * 地址
	 */
	private String url;

}
