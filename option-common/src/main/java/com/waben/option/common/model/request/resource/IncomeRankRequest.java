package com.waben.option.common.model.request.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeRankRequest {

    private int id;

    private String name;

    private BigDecimal amount;

    private Integer inviteNumber;

    private BigDecimal income;

    private String type;

    private String headImg;

}
