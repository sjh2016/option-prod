package com.waben.option.common.model.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentAdminStaDTO {

	/**
	 * 成功充值笔数
	 */
	private long successCount;
	/**
	 * 实际支付币种数量总和
	 */
	private BigDecimal realMoneyTotal;
	/**
	 * 实际到账币种数量总和
	 */
	private BigDecimal realNumTotal;
	/**
	 * 手续费总和
	 */
	private BigDecimal feeTotal;

}
