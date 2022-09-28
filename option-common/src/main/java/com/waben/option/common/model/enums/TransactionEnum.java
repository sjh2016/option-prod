package com.waben.option.common.model.enums;

public enum TransactionEnum {

	CREDIT_PAYMENT(CreditDebitEnum.CREDIT, "充值"),

	CREDIT_PROFIT(CreditDebitEnum.CREDIT, "发电收益"),

	CREDIT_RETURN_WAGER(CreditDebitEnum.CREDIT, "到期退回投注本金"),

	CREDIT_LOGIN_PROFIT(CreditDebitEnum.CREDIT, "登录收益"),

	CREDIT_INVITE_REGISTER(CreditDebitEnum.CREDIT, "邀请注册奖励"),

	CREDIT_INVITE_WAGER(CreditDebitEnum.CREDIT, "邀请投注奖励"),

	CREDIT_ACTIVITY_WAGER(CreditDebitEnum.CREDIT, "活动投注奖励"),

	CREDIT_LUCKY_DRAW(CreditDebitEnum.CREDIT, "中奖"),

	CREDIT_SUBORDINATE(CreditDebitEnum.CREDIT, "下级收益"),

	CREDIT_MOVEMENT(CreditDebitEnum.CREDIT, "上分"),

	CREDIT_REGISTER_GIFT(CreditDebitEnum.CREDIT, "注册奖励"),

	CREDIT_INVESTMENT_GIFT(CreditDebitEnum.CREDIT, "首次投资奖励"),

	CREDIT_SUNSHINE(CreditDebitEnum.CREDIT, "晒单收益"),

	CREDIT_WAGER_RETURN(CreditDebitEnum.CREDIT, "投注退回"),

	DEBIT_MOVEMENT(CreditDebitEnum.DEBIT, "下分"),

	DEBIT_WAGER(CreditDebitEnum.DEBIT, "投注"),

	DEBIT_LUCKY_DRAW(CreditDebitEnum.DEBIT, "活动抽奖"),

	DEBIT_BUY_GOOD(CreditDebitEnum.DEBIT, "商品预购"),

	DEBIT_WITHDRAW_OTC(CreditDebitEnum.DEBIT, "提现"),

	FREEZE_WITHDRAW_OTC(CreditDebitEnum.FREEZE, "提现冻结"),

	UNFREEZE_RETURN_WITHDRAW_OTC(CreditDebitEnum.UNFREEZE, "提现解冻（失败）"),

	UNFREEZE_WITHDRAW_OTC(CreditDebitEnum.UNFREEZE, "提现解冻"),

	CREDIT_WAGER(CreditDebitEnum.CREDIT, "投资上分收益"),

	;

	private final CreditDebitEnum creditDebitType;

	private final String description;

	/*
	 * TransactionEnum(CreditDebitEnum creditDebitType) { this.creditDebitType =
	 * creditDebitType; this.description = ""; }
	 */

	TransactionEnum(CreditDebitEnum creditDebitType, String description) {
		this.creditDebitType = creditDebitType;
		this.description = description;
	}

	public CreditDebitEnum getCreditDebitType() {
		return creditDebitType;
	}

	public String getDescription() {
		return description;
	}

}
