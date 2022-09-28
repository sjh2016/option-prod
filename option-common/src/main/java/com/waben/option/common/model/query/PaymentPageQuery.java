package com.waben.option.common.model.query;

import lombok.Data;

/**
 * @author: Peter
 * @date: 2021/6/12 16:00
 */
@Data
public class PaymentPageQuery {

    private Long userId;
    private String username;
    private String country;
    private String operatorName;
    private String paymentName;
    private String status;
    private int page;
    private int size;
}
