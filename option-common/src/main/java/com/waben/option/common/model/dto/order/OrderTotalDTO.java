package com.waben.option.common.model.dto.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 10:00
 */
@Data
public class OrderTotalDTO {

    private BigDecimal profit;

    private BigDecimal dayProfit;
}
