package com.waben.option.common.model.dto.user;

import com.waben.option.common.model.enums.BurseTypeEnum;
import com.waben.option.common.model.enums.CurrencyEnum;

import lombok.Data;

@Data
public class UserBurseDTO {

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

}
