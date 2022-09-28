package com.waben.option.common.model.request.point;

import com.waben.option.common.model.enums.ProductOrderStatusEnum;

import lombok.Data;

@Data
public class PointAuditOrderRequest {

	/** 产品订单ID */
	private Long id;

	/** 审核状态 */
	private ProductOrderStatusEnum status;

}
