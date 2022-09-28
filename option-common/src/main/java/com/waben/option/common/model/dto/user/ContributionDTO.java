package com.waben.option.common.model.dto.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContributionDTO {

    private BigDecimal totalContribution = BigDecimal.ZERO;

    private BigDecimal yesterdayContribution = BigDecimal.ZERO;

}
