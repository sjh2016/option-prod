package com.waben.option.common.model.dto.push;

import lombok.Data;

@Data
public class OutsidePushMessageTemplateDTO {

	/**
	 * 标题
	 */
	private String title;
	/**
	 * 消息
	 * <p>
	 * 如果有动态参数，使用{key}占位，例如：您的账户于 {time} 成功充值 {amount}USDT。
	 * </p>
	 */
	private String message;

}
