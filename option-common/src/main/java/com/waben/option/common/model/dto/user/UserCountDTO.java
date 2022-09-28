package com.waben.option.common.model.dto.user;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCountDTO {

    private int personCount;

    private int pendingPersonCount;

    private BigDecimal totalAmount;

}
