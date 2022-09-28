package com.waben.option.mode.gateway.order;

import lombok.Data;

@Data
public class GatewayDynamicProductOrderPageDataReq {

	/**
	 * 页码
	 */
	private int page;
	/**
	 * 每页大小
	 */
	private int size;

}
