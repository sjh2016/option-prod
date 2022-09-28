package com.waben.option.common.model.dto.user;

import lombok.Data;

/**
 * 用户所属马甲
 */
@Data
public class UserVestDTO {

	/** 用户ID */
	private Long userId;
	/** 设备类型，1IOS 2安卓 */
	private Integer deviceType;
	/** 马甲包序号 */
	private Integer vestIndex;

}
