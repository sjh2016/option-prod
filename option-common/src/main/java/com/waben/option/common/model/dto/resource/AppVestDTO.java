package com.waben.option.common.model.dto.resource;

import lombok.Data;

@Data
public class AppVestDTO {

	/**
	 * 设备类型
	 * <ul>
	 * <li>1 安卓</li>
	 * <li>2 IOS</li>
	 * </ul>
	 */
	private Integer type;
	/**
	 * 序号
	 */
	private Integer shellIndex;
	/**
	 * 链接
	 */
	private String url;
	/**
	 * 开关标识
	 */
	private Boolean flag;
	/**
	 * 版本号
	 */
	private Integer versionCode;

}
