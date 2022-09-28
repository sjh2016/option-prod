package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestmentDTO {

    /**
     * 投资金额
     */
    private BigDecimal amount;

    /**
     * 回报率
     */
    private BigDecimal responseRate;

    /**
     * 每日收益
     */
    private BigDecimal income;

    /**
     * 回本周期
     */
    private int cycle;

    /**
     * 锁定期
     */
    private int lockPeriod;

}
