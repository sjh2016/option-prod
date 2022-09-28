package com.waben.option.common.model.dto.resource;

import java.time.LocalDateTime;

import com.waben.option.common.model.enums.NewsTypeEnum;

import lombok.Data;

@Data
public class NewsDTO {

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 编号
	 */
	private String newsNo;
	/**
	 * 类型
	 */
	private NewsTypeEnum type;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 封面
	 */
	private String coverImg;
	/**
	 * 发布时间
	 */
	private LocalDateTime publishTime;

}
