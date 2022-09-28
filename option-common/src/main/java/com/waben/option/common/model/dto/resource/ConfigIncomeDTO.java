package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfigIncomeDTO {

    private BigDecimal oneIncome = BigDecimal.ZERO;

    private BigDecimal twoIncome = BigDecimal.ZERO;

    private BigDecimal threeIncome = BigDecimal.ZERO;

}
