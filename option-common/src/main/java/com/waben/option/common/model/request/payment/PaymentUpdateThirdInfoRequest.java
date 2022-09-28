package com.waben.option.common.model.request.payment;

import lombok.Data;

@Data
public class PaymentUpdateThirdInfoRequest {

	/**
	 * 订单ID
	 */
	private Long id;
	/**
	 * 第三方单号
	 */
	private String thirdOrderNo;

}
