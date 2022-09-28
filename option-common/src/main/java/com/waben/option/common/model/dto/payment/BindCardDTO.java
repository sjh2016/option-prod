package com.waben.option.common.model.dto.payment;

import lombok.Data;

/**
 * 绑卡信息
 */
@Data
public class BindCardDTO {

	private Long id;
	/** 用户ID */
	private Long userId;
	/** 持卡人姓名 */
	private String name;
	/** 银行名称 */
	private String bankName;
	/** 支行名称 */
	private String branchName;
	/** 银行卡号 */
	private String bankCardId;
	/** 银行编码 */
	private String bankCode;
	/** 电话号码 */
	private String mobilePhone;
	/** 支持的通道ID（英文逗号分割） */
	private String supportUpId;
	/** 支付的通道银行代码（英文逗号分割） */
	private String supportUpCode;

}
