package com.waben.option.common.model.dto.activity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class AllowanceDTO {

	private Integer type;
	
	private BigDecimal total;

	private BigDecimal distributed;

}
