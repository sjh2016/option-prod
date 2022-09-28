package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.AccountMovementStatusEnum;
import com.waben.option.common.model.enums.CreditDebitEnum;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 资金账户上分/下分
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_actual_account_movement")
public class AccountMovement extends BaseEntity<Long> {

    /**
     * 账户ID
     */
    private Long accountId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 货币
     */
    private CurrencyEnum currency;
    /**
     * 上下分类型
     */
    private CreditDebitEnum creditDebit;
    /**
     * 状态
     */
    private AccountMovementStatusEnum status;
    /**
     * 申请人ID
     */
    private Long applyUserId;
    /**
     * 申请人用户名
     */
    private String applyUsername;
    /**
     * 申请上下分说明
     */
    private String applyRemark;
    /**
     * 审核人ID
     */
    private Long auditUserId;
    /**
     * 审核用户名
     */
    private String auditUsername;
    /**
     * 审核上下分说明
     */
    private String auditRemark;
    /**
     * 审核时间
     */
    private LocalDateTime gmtAudit;

    public static final String ACCOUNT_ID = "account_id";
    public static final String USER_ID = "user_id";
    public static final String BROKER_SYMBOL = "broker_symbol";
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String CREDIT_DEBIT = "credit_debit";
    public static final String STATUS = "status";
    public static final String APPLY_USER_ID = "apply_user_id";
    public static final String APPLY_USERNAME = "apply_username";
    public static final String APPLY_REMARK = "apply_remark";
    public static final String AUDIT_USER_ID = "audit_user_id";
    public static final String AUDIT_USERNAME = "audit_username";
    public static final String AUDIT_REMARK = "audit_remark";
    public static final String GMT_AUDIT = "gmt_audit";

}
