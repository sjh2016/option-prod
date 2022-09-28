package com.waben.option.common.model.request.account;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@SuperBuilder
public class InitAccountRequest {

    private Long userId;
    private BigDecimal amount;
    private CurrencyEnum currency;
}
