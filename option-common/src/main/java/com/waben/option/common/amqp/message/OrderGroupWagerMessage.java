package com.waben.option.common.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderGroupWagerMessage {

    private BigDecimal actualAmount;

    private Long userId;

    private Integer groupIndex;

    private Long orderId;

}
