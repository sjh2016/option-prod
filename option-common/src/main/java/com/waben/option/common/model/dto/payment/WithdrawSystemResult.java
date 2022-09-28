package com.waben.option.common.model.dto.payment;

import lombok.Data;

@Data
public class WithdrawSystemResult {

	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 上游返回信息 */
	private String thirdRespMsg;
	/** 是否提交成功即为提现成功 */
	private boolean immediateSuccess; 

}
