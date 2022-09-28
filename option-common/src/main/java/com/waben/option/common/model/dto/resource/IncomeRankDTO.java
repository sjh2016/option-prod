package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IncomeRankDTO {

    private int id;

    private String name;

    private BigDecimal amount;

    private Integer inviteNumber;

    private BigDecimal income;

    private String type;

    private String headImg;

}
