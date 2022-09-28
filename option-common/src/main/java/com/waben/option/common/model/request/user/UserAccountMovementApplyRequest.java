package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAccountMovementApplyRequest {

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 上下分类型
	 */
	private CreditDebitEnum creditDebit;
	/**
	 * 金额
	 */
	private BigDecimal amount;
	/**
	 * 货币
	 */
	private CurrencyEnum currency;
	/**
	 * 申请上下分说明
	 */
	private String applyRemark;

}
