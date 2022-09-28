package com.waben.option.common.model.request.order;

import lombok.Data;

@Data
public class UploadUserPurchaseGoodsImageRequest {

    private Long id;

    private Long userId;

    private String picture0;

    private String picture1;

    private String picture2;

    private String picture3;

}
