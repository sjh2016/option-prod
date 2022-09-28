package com.waben.option.common.model.request.order;

import com.waben.option.common.model.enums.OrderStatusEnum;
import lombok.Data;

/**
 * @author: Peter
 * @date: 2021/6/23 9:50
 */
@Data
public class UpdateOrderRequest {

    private Long id;

    private OrderStatusEnum status;
}
