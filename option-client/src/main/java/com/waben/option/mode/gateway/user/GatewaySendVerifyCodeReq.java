package com.waben.option.mode.gateway.user;

import com.waben.option.common.model.enums.RegisterEnum;

import lombok.Data;

@Data
public class GatewaySendVerifyCodeReq {

	/**
	 * 类型
	 */
	private RegisterEnum type;
	/**
	 * 区号
	 */
	private String areaCode;
	/**
	 * 用户名
	 */
	private String username;

}
