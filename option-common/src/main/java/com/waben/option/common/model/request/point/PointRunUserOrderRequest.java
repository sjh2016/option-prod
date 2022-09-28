package com.waben.option.common.model.request.point;

import java.util.List;

import com.waben.option.common.model.enums.RunOrderStatusEnum;

import lombok.Data;

@Data
public class PointRunUserOrderRequest {

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * 状态
	 */
	private List<RunOrderStatusEnum> statusList;
	
	/**
	 * 页码
	 */
	private int page;
	
	/**
	 * 每页大小
	 */
	private int size;

}
