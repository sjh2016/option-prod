package com.waben.option.common.model.enums;

public enum NewsTypeEnum {

	ECONOMY("经济"),
	
	BUSINESS("商业");

	private String description;

	NewsTypeEnum(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
