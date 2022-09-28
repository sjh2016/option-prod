package com.waben.option.common.amqp.message;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunOrderSettlementMessage {

	/**
	 * 是否生成的
	 */
	private Boolean isGenerate = false;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 上级用户ID
	 */
	private Long parentId;
	/**
	 * 产品订单ID
	 */
	private Long productOrderId;
	/**
	 * 跑分订单ID
	 */
	private Long runOrderId;
	/**
	 * 收益
	 */
	private BigDecimal profit;
	/**
	 * 是否需要给上级分成
	 */
	private boolean needDivide;
	/**
	 * 给上级的收益分成比例
	 */
	private BigDecimal profitDivideRatio;
	/**
	 * 给上级的分成金额
	 */
	private BigDecimal profitDivide;

}
