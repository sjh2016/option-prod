package com.waben.option.mode.gateway.order;

import com.waben.option.common.model.enums.OrderStatusEnum;

import lombok.Data;

@Data
public class GatewayProductOrderPageDataReq {

	/**
	 * 订单状态
	 */
	private OrderStatusEnum status;
	/**
	 * 页码
	 */
	private int page;
	/**
	 * 每页大小
	 */
	private int size;

}
