package com.waben.option.common.model.dto.push;

import lombok.Data;

import java.util.List;

@Data
public class OutsideNoticeDTO {

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;
	/**
	 * 用户ID列表，如果为null或者空表示广播
	 */
	private List<Long> userIds;

}
