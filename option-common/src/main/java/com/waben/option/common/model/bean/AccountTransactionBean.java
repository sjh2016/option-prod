package com.waben.option.common.model.bean;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@SuperBuilder
public class AccountTransactionBean implements Serializable {

    private static final long serialVersionUID = 5121610014163752340L;

    private long userId;

    private TransactionEnum type;

    private BigDecimal amount;

    private Long transactionId;

    private String remark;

    private CurrencyEnum currency;

    private LocalDateTime time;

}
