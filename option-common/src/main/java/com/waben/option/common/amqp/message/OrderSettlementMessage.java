package com.waben.option.common.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Peter
 * @date: 2021/6/24 16:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSettlementMessage {

    private Long orderId;
}
