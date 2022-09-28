package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_u_user_burse")
public class UserBurse extends BaseEntity<Long> {

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 钱包地址
	 */
	private String address;
	/**
	 * 货币
	 */
	private CurrencyEnum currency;
	/**
	 * 钱包类型
	 */
	private BurseTypeEnum burseType;
	/**
	 * 支付apiID
	 */
	private Long payApiId;

	public static final String USER_ID = "user_id";
	public static final String ADDRESS = "address";
	public static final String CURRENCY = "currency";
	public static final String BURSE_TYPE = "burse_type";
	public static final String PAY_API_ID = "pay_api_id";

}
