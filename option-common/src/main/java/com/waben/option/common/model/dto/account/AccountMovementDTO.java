package com.waben.option.common.model.dto.account;

import com.waben.option.common.model.enums.AccountMovementStatusEnum;
import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountMovementDTO {
	
	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 账户id
	 */
	private Long accountId;
	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 用户账号
	 */
	private String username;
	/**
	 * 用户姓名
	 */
	private String name;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 货币
	 */
	private CurrencyEnum currency;
	/**
	 * 上下分类型
	 */
	private CreditDebitEnum creditDebit;
	/**
	 * 状态
	 */
	private AccountMovementStatusEnum status;
	/**
	 * 申请人ID
	 */
	private Long applyUserId;
	/**
	 * 申请人用户名
	 */
	private String applyUsername;
	/**
	 * 申请上下分说明
	 */
	private String applyRemark;
	/**
	 * 审核人ID
	 */
	private Long auditUserId;
	/**
	 * 审核用户名
	 */
	private String auditUsername;
	/**
	 * 审核上下分说明
	 */
	private String auditRemark;
	/**
	 * 审核时间
	 */
	private LocalDateTime gmtAudit;
	/**
	 * 申请时间
	 */
	private LocalDateTime gmtCreate;
	
}
