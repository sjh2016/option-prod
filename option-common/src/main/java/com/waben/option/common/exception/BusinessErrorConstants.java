package com.waben.option.common.exception;

public enum BusinessErrorConstants {

	ERROR_SYSTEM(1000, "系统异常"),

	ERROR_PARAM_FORMAT(1001, "参数格式错误"),

	ERROR_FILE_UPLOAD_FAIL(1010, "文件上传失败"),

	ERROR_SMS_SEND_FAIL(1032, "短信发送失败"),

	ERROR_DATA_NOT_FOUND(1062, "数据不存在"),

	/******************************* 支付模块 start *****************************/

	ERROR_PAYMENT_REQUEST_FAIL_WITH_MSG(2001, "充值请求失败, {0}"),

	ERROR_WITHDRAW_REQ_FAIL_WITH_MSG(2002, "提现请求失败, {0}"),

	ERROR_PAYMENT_REQUEST_FAIL(2057, "充值请求失败"),

	ERROR_WITHDRAW_REQ_FAIL(2058, "提现请求失败"),

	ERROR_PAYMENT_BANK_CODE_EMPTY(2059, "支付银行不能为空"),
	
	ERROR_WITHDRAW_IFSC_EMPTY(2062, "IFSC支行号码不能为空"),

	/******************************* 支付模块 end *****************************/

	/******************************* 活动模块 start *****************************/

	ERROR_ACTIVITY_JOIN_SUCCESS(5014, "恭喜，已成功参与"),

	ERROR_ACTIVITY_JOIN_ALREADY(5015, "您已参与，去投资更多，提高自己的获奖几率"),

	ERROR_ACTIVITY_JOIN_NOT_REACH(5016, "暂无参与资格，去投资更多，获取资格"),

	/******************************* 活动模块 end *****************************/

	/******************************* OTC模块 start *****************************/

	ERROR_PRODUCT_NOTEXIST_OR_UNONLINE(6001, "产品不存在或者未上线"),

	ERROR_PRODUCT_SELLOUT(6002, "该产品当天已售罄，请明天再来购买"),

	ERROR_PRODUCT_ORDER_NOTEXIST(6003, "产品订单不存在"),

	ERROR_PRODUCT_ORDER_STATUS_NOTMATCH(6004, "产品订单状态不匹配"),

	ERROR_OPERATE_USER_NOTMATCH(6005, "操作用户不匹配"),

	ERROR_RUN_ORDER_NOTEXIST(6006, "兑换订单不存在"),

	ERROR_RUN_ORDER_STATUS_NOTMATCH(6007, "兑换订单状态不匹配"),

	ERROR_RUN_MERCHANT_NO_MATCH(6008, "当前无匹配的商家，请稍候再试"),

	ERROR_RUN_MERCHANT_NOTEXIST(6009, "兑换商家不存在"),

	ERROR_RUN_ORDER_NO_MATCH(6010, "当前无匹配的兑换订单，请明天再试或者发起质押申请"),

	ERROR_RUN_ORDER_AMOUNT_NOT_MATCH(6011, "兑换金额不匹配"),

	ERROR_RUN_MERCHANT_AMOUNT_NOTENOUGH(6012, "商家可兑换金额不足"),

	/******************************* OTC模块 end *****************************/

	;

	private int code;

	private String desc;

	private BusinessErrorConstants(int code, String desc) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

}
