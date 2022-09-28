package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TreasureChestUserJoinDTO {

	/** 用户UID */
	private String uid;
	/** 金额 */
	private BigDecimal amount;
	/** 日期 */
	private String day;

}
