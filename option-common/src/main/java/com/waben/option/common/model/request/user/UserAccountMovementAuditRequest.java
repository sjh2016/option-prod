package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.AccountMovementStatusEnum;
import lombok.Data;

@Data
public class UserAccountMovementAuditRequest {

	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 状态
	 */
	private AccountMovementStatusEnum status;
	/**
	 * 审核上下分说明
	 */
	private String auditRemark;

}
