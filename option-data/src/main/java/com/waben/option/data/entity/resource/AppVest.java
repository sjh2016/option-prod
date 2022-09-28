package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_app_vest", autoResultMap = true)
public class AppVest extends BaseTemplateEntity {

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

	public static final String TYPE = "type";
	public static final String SHELL_INDEX = "shell_index";

}
