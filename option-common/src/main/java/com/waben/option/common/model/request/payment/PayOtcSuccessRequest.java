package com.waben.option.common.model.request.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayOtcSuccessRequest {

	/** 订单编号 */
	private String orderNo;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 实际支付币种数量 */
	private BigDecimal realMoney;

}
