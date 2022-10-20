package com.waben.option.common.model.dto.account;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserAccountDTO {

	/**
	 * 账户ID
	 */
	private Long id;
	
	/**
	 * 用户ID
	 */
    private Long userId;

    /**
     * 用户余额
     */
    private BigDecimal balance;

    /**
     * 用户佣金
     */
    private BigDecimal commission;
    /**
     * 用户佣金
     */
    private BigDecimal secondCommission;
    /**
     * 用户佣金
     */
    private BigDecimal thridCommission;

    /**
     * 冻结资金
     */
    private BigDecimal freezeCapital;


    /**
     * 币种
     */
    private CurrencyEnum currency;
    
    /**
     * 用户组
     */
    private Integer groupIndex;

    private BigDecimal totalRechargeAmount;
}
