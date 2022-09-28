package com.waben.option.common.model.dto.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserStaDTO {

    /**
     * 总计投资金额
     */
    private BigDecimal sumAmount;
    /**
     * 累计收益
     */
    private BigDecimal sumProfit;
    /**
     * 每日收益
     */
    private BigDecimal perProfit;
    /**
     * 是否领取了赠送的订单
     */
    private Boolean hasGiveOrder;
    /**
     * 邀请人数
     */
    private Integer inviteCount;
    /**
     * 邀请收益
     */
    private BigDecimal inviteIncome;
    /**
     * 用户余额
     */
    private BigDecimal balance;
    /**
     * 冻结资金
     */
    private BigDecimal freezeCapital;

    /**
     * 下级用户总数
     */
    private Integer personCount;

}
