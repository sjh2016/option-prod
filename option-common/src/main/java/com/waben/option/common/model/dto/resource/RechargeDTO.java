package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeDTO {

    private Integer id;

    /**
     * 运营商ID
     */
    private Integer operatorId;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 服务费
     */
    private BigDecimal serviceCharge;

    /**
     * 支付货币
     */
    private String currency;

}
