package com.waben.option.common.model.dto.resource;

import lombok.Data;

@Data
public class BankCodeDTO {

	private int id;

	private String name;

	private String code;

	private String currency;
	/** 支持的通道ID（英文逗号分割） */
	private String supportUpId;
	/** 支付的通道银行代码（英文逗号分割） */
	private String supportUpCode;

}
