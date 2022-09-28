package com.waben.option.common.model.dto.point;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PointProductOrderUserStaDTO {

	/**
	 * 总计质押金额
	 */
	private BigDecimal sumAmount;
	/**
	 * 总兑换收益
	 */
	private BigDecimal sumProfit;

}
