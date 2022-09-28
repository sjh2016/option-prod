package com.waben.option.common.model.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台充值/提现日统计
 */
@Data
public class PlatformPaymentDayStatisticsDTO {

	/**
	 * OTC充值金额
	 */
	private BigDecimal paymentOtcTotal;
	/**
	 * 钱包充值金额
	 */
	private BigDecimal paymentCoinTotal;
	/**
	 * 首次充值人数（当日）
	 */
	private Long firstPaymentCount;
	/**
	 * 首次充值金额(总)
	 */
	private BigDecimal firstPaymentTotal;
	/**
	 * 充值人数（当日）
	 */
	private Long paymentCount;
	/**
	 * 充值金额(总)
	 */
	private BigDecimal paymentTotal;
	/**
	 * OTC提现金额(线上)
	 */
	private BigDecimal withdrawOtcOnlineTotal;
	/**
	 * 钱包提现金额(线上)
	 */
	private BigDecimal withdrawCoinOnlineTotal;
	/**
	 * 提现金额(线下)
	 */
	private BigDecimal withdrawOfflineTotal;
	/**
	 * 提现人数（当日）
	 */
	private Long withdrawCount;
	/**
	 * 提现金额(总)
	 */
	private BigDecimal withdrawTotal;

}
