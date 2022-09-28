package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AllowanceDetailDTO {

	private Long userId;

	private Integer cycle;

	private String name;

	private BigDecimal returnRate;

	private BigDecimal amount;
	/** 整个周期收益的80% */
	private BigDecimal distributed;

}
