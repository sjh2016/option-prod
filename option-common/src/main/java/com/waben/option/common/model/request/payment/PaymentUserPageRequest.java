package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentUserPageRequest {

	/**
	 * 出入金类型
	 */
	private PaymentCashType cashType;
	/**
	 * 状态
	 */
	private List<PaymentOrderStatusEnum> statusList;
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
	private LocalDateTime start;
	/**
	 * 结束时间
	 */
	private LocalDateTime end;

}
