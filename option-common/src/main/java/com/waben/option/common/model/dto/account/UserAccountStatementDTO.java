package com.waben.option.common.model.dto.account;

import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserAccountStatementDTO {

    private Long id;

    private Long userId;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 用户余额
     */
    private BigDecimal balance;

    /**
     * 冻结资金
     */
    private BigDecimal freezeCapital;

    /**
     * 流水类型
     */
    private TransactionEnum type;

    /**
     * 资金变化类型
     */
    private CreditDebitEnum creditDebit;

    /**
     * 关联
     */
    private Long transactionId;

    /**
     * 产生流水的唯一id，防止mq重复调用
     */
    private Long uniqueId;

    /**
     * 币种
     */
    private CurrencyEnum currency;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 用户名称
     */
    private String username;
}
