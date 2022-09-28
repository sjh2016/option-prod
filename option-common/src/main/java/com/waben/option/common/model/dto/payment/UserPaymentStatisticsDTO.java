package com.waben.option.common.model.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用户充值/提现统计
 */
@Data
public class UserPaymentStatisticsDTO {
	
	/**
	 * 提现冻结金额
	 */
	private BigDecimal withdrawFreezeCapital;
	/**
	 * OTC充值金额
	 */
	private BigDecimal paymentOtcTotal;
	/**
	 * OTC提现金额
	 */
	private BigDecimal withdrawOtcTotal;
	/**
	 * 钱包充值金额
	 */
	private BigDecimal paymentCoinTotal;
	/**
	 * 钱包提币金额
	 */
	private BigDecimal withdrawCoinTotal;
	/**
	 * 上分金额
	 */
	private BigDecimal movementCreditTotal;
	/**
	 * 下分金额
	 */
	private BigDecimal movementDebitTotal;
}
