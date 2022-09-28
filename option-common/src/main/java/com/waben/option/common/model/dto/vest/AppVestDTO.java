package com.waben.option.common.model.dto.vest;

import lombok.Data;

@Data
public class AppVestDTO {

	/** 设备类型，1IOS 2安卓 */
	private Integer deviceType;
	/** 马甲包序号 */
	private Integer vestIndex;
	/** 推送key */
	private String jPushAppKey;
	/** 推送secret */
	private String jPushSecret;
	
}
