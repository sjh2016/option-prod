package com.waben.option.data.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 绑卡信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_bind_card")
public class BindCard extends BaseEntity<Long> {
	
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
	
	public static final String USER_ID = "user_id";
	public static final String BANK_CARD_ID = "bank_card_id";
	public static final String PAY_API_ID = "pay_api_id";
	
}
