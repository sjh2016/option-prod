package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.AccountMovementStatusEnum;
import com.waben.option.common.model.enums.CreditDebitEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserAccountMovementRequest {

	private String id;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 用户ID列表
	 */
	private List<Long> uidList;
	/**
	 * 状态
	 */
	private List<AccountMovementStatusEnum> statusList;
	/**
	 * 上下分类型
	 */
	private CreditDebitEnum creditDebit;
	/**
	 * 申请开始时间
	 */
	private LocalDateTime startTime;
	/**
	 * 申请结束时间
	 */
	private LocalDateTime endTime;
	/**
	 * 审核开始时间
	 */
	private LocalDateTime auditStart;
	/**
	 * 审核结束时间
	 */
	private LocalDateTime auditEnd;
	/**
	 * 页码
	 */
	private int page;
	/**
	 * 每页大小
	 */
	private int size;

}
