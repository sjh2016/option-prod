package com.waben.option.common.model.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/27 21:08
 */
@Data
public class OrderProfitDTO {

    private Long userId;

    private BigDecimal profit;
}
