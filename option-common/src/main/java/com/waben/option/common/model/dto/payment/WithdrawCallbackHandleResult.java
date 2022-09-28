package com.waben.option.common.model.dto.payment;

import lombok.Data;

@Data
public class WithdrawCallbackHandleResult {

	/**
	 * 状态
	 * <ul>
	 * <li>1成功</li>
	 * <li>2失败</li>
	 * <li>3其他</li>
	 * </ul>
	 */
	private int state;
	/** 返回上游的数据 */
	private String backThirdData;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 交易hash（提币才有值） */
	private String hash;

	public WithdrawCallbackHandleResult() {
	}

	public WithdrawCallbackHandleResult(int state, String backThirdData) {
		super();
		this.state = state;
		this.backThirdData = backThirdData;
	}

}
