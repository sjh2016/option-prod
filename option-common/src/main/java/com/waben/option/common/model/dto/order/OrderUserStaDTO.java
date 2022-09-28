package com.waben.option.common.model.dto.order;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderUserStaDTO {

	/**
	 * 总计投资金额
	 */
	private BigDecimal sumAmount;
	/**
	 * 累计收益
	 */
	private BigDecimal sumProfit;
	/**
	 * 每日收益
	 */
	private BigDecimal perProfit;
	/**
	 * 是否领取了赠送的订单
	 */
	private Boolean hasGiveOrder;

}
