package com.waben.option.common.model.request.payment;

import lombok.Data;

@Data
public class WithdrawUnderlineNotpassRequest {

	/** 提现订单ID */
	private Long id;
	/** 说明 */
	private String remark;

}
