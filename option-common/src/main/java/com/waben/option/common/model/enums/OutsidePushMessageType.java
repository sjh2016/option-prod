package com.waben.option.common.model.enums;

public enum OutsidePushMessageType {

	PAYMENT_SUCCESS("paymentSuccess"),

	WITHDRAW_SUCCESS("withdrawSuccess"),
	
	LEVEL_UP("levelUp"),
	
	WAGER("wager"),
	
	WAGER_SETTLE("wagerSettle");

	private String key;

	private OutsidePushMessageType(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
