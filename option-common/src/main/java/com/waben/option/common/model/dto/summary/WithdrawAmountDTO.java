package com.waben.option.common.model.dto.summary;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/11 4:06
 */
@Data
public class WithdrawAmountDTO {

    private Long userId;

    private BigDecimal amount = BigDecimal.ZERO;
}
