package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserDataDTO {

    private Long id;

    /**
     * 日期
     */
    private String day;

    /**
     * 注册人数
     */
    private int registerNumber;

    /**
     * 入金金额
     */
    private BigDecimal paymentAmount;

    /**
     * 入金人数
     */
    private Integer paymentUserCount;

    /**
     * 被邀请人数
     */
    private int beInvites;

    /**
     * 被邀请人入金
     */
    private BigDecimal beInvitesPaymentAmount;

}
