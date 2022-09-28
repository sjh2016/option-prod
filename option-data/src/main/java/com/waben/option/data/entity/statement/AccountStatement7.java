package com.waben.option.data.entity.statement;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.TransactionEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_account_statement7")
public class AccountStatement7 extends BaseEntity<Long> {

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

    public static final String ACCOUNT_ID = "account_id";

    public static final String TYPE = "type";

    public static final String CURRENCY = "currency";

    public static final String USER_ID = "user_id";

}
