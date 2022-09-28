package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import lombok.Data;

import java.util.List;

@Data
public class WithdrawUserPageRequest {

	/**
	 * 出入金类型
	 */
	private PaymentCashType cashType;
	/**
	 * 状态
	 */
	private List<WithdrawOrderStatusEnum> statusList;
	/**
	 * 页码
	 */
	private int page;
	/**
	 * 每页大小
	 */
	private int size;
	/**
	 * 开始时间
	 */
	private Long start;
	/**
	 * 结束时间
	 */
	private Long end;

}
