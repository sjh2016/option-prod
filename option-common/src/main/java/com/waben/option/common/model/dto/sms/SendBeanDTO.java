package com.waben.option.common.model.dto.sms;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@SuperBuilder
public class SendBeanDTO {

    private String sendBeanName;

    private BigDecimal maxLimitCount;

    private BigDecimal rate;
}
