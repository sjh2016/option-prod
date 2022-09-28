package com.waben.option.mode.gateway.user;

import lombok.Data;

@Data
public class GatewayLoginReq {

	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;

}
