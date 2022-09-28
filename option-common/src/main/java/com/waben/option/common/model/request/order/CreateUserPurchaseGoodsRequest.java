package com.waben.option.common.model.request.order;

import lombok.Data;

@Data
public class CreateUserPurchaseGoodsRequest {

    private Long id;

    private Long userId;

    private String name;

    private String surname;

    private String streetName;

    private String urbanArea;

    private String province;

    private String phone;

    private String postCode;

}
