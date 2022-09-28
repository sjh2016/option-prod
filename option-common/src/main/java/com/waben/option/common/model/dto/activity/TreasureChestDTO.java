package com.waben.option.common.model.dto.activity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TreasureChestDTO {

	/** 密码 */
	private String password;
	/** 开始时间 */
	private LocalDateTime startTime;
	/** 结束时间 */
	private LocalDateTime endTime;
	/** 是否上线 */
	private Boolean online;
	/** 最大可抽奖次数 */
	private Integer limitQuantity;
	/** 已抽奖次数 */
	private Integer usedQuantity;
	/** 中奖概率设置 */
	private String probability;

}
