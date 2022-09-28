package com.waben.option.mode.gateway.user;

import com.waben.option.common.model.enums.RegisterEnum;

import lombok.Data;

@Data
public class GatewayRegisterReq {

	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 注册类型
	 */
	private RegisterEnum registerType;
	/**
	 * 验证码
	 */
	private String verifyCode;
	/**
	 * 区号
	 */
	private String areaCode;
	/**
	 * 层级邀请码
	 */
	private String symbolCode;
	/**
	 * 图片验证码会话ID
	 */
	private String sessionId;

}
