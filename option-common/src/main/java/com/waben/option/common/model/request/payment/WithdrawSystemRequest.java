package com.waben.option.common.model.request.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawSystemRequest {

	/** 通道ID */
	private Long passagewayId;
	/** 提现订单ID */
	private Long id;
	/** 提现到账数量（扣除手续费） */
	private BigDecimal realNum;

}
