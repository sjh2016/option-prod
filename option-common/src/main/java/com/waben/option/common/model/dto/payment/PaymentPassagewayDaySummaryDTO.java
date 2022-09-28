package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.PaymentPassagewayDaySummaryType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 支付通道日汇总
 */
@Data
public class PaymentPassagewayDaySummaryDTO {

	/**
	 * 日期
	 */
	private LocalDate day;
	/**
	 * 支付Api通道ID
	 */
	private Long payApiId;
	/**
	 * 支付Api通道名字
	 */
	private String payApiName;
	/**
	 * 统计类型
	 */
	private PaymentPassagewayDaySummaryType type;
	/**
	 * 金额
	 */
	private BigDecimal amount;

}
