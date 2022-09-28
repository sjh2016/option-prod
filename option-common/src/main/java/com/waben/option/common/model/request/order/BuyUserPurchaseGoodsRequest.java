package com.waben.option.common.model.request.order;

import com.waben.option.common.model.enums.PurchaseGoodEnum;
import lombok.Data;

@Data
public class BuyUserPurchaseGoodsRequest {

    private Long id;

    private Long userId;

    private PurchaseGoodEnum type;
    
    private Integer quantity;

}
