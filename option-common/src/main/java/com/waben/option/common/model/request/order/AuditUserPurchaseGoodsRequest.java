package com.waben.option.common.model.request.order;

import com.waben.option.common.model.enums.PurchaseGoodStatusEnum;
import lombok.Data;

@Data
public class AuditUserPurchaseGoodsRequest {

    private Long id;

    private PurchaseGoodStatusEnum status;

}
