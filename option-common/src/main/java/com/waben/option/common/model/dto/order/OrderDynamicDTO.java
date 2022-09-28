package com.waben.option.common.model.dto.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDynamicDTO {

	private String uid;
	private Long commodityId;
	private Integer cycle;
	private String name;
	private BigDecimal returnRate;
	private BigDecimal amount;

}
