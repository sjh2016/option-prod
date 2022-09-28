package com.waben.option.data.entity.resource;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.NewsTypeEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_news")
public class News extends BaseEntity<Long> {

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

	public static final String NEWS_NO = "news_no";
	
	public static final String PUBLISH_TIME = "publish_time";

}
