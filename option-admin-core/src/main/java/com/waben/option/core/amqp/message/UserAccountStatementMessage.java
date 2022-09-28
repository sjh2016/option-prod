package com.waben.option.core.amqp.message;

import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccountStatementMessage {

    private Long userId;

    private Long accountId;

    private CreditDebitEnum creditDebit;

    private BigDecimal amount;

    private BigDecimal balance;

    private TransactionEnum type;

    private CurrencyEnum currency;

    private BigDecimal freezeCapital;

    private String remark;

    private Long transactionId;

    private LocalDateTime transactionBeanTime;
}
