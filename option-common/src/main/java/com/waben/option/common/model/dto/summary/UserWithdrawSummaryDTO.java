package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/11 2:15
 */
@Data
public class UserWithdrawSummaryDTO {

    private Long userId;

    private BigDecimal totalInviteAmount = BigDecimal.ZERO;

    private BigDecimal totalLoginAmount = BigDecimal.ZERO;

    private BigDecimal totalDivideAmount = BigDecimal.ZERO;
}
