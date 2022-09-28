package com.waben.option.mode.gateway.payment;

import lombok.Data;

@Data
public class GatewayUpdateBindCardReq {
	
	private Long id;
	/** 持卡人姓名 */
	private String name;
	/** 银行编码 */
	private String bankCode;
	/** 支行名称 */
	private String branchName;
	/** 银行卡号 */
	private String bankCardId;
	/** 电话号码 */
	private String mobilePhone;
	
}
