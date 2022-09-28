package com.waben.option.data.entity.user;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_account")
public class Account extends BaseEntity<Long> {

    public static final String BALANCE = "balance";
    public static final String COMMISSION = "commission";
    public static final String SECOND_COMMISSION = "second_commission";
    public static final String THRID_COMMISSION = "thrid_commission";
    public static final String FREEZE_CAPITAL = "freeze_capital";
    public static final String CURRENCY = "currency";
    public static final String USER_ID = "user_id";
    private long userId;
    /**
     * 用户余额包含佣金
     */
    private BigDecimal balance;
    /**
     * 用户佣金
     */
    private BigDecimal commission;
    /**
     * 用户佣金
     */
    private BigDecimal secondCommission;
    /**
     * 用户佣金
     */
    private BigDecimal thridCommission;
    /**
     * 冻结资金
     */
    private BigDecimal freezeCapital;
    /**
     * 币种
     */
    private CurrencyEnum currency;
    /**
     * 用户组
     */
    private Integer groupIndex;

    /**
     * 存入
     *
     * @param amount
     */
    public void credit(BigDecimal amount) {
        if (null == this.balance) {
            this.balance = BigDecimal.ZERO;
        }
        this.balance = this.balance.add(amount);
    }

    public void commission(BigDecimal amount) {
        if (null == this.commission) {
            this.commission = BigDecimal.ZERO;
        }
        this.commission = this.commission.add(amount);
    }

    public void secondCommission(BigDecimal amount) {
        if (null == this.secondCommission) {
            this.secondCommission = BigDecimal.ZERO;
        }
        this.secondCommission = this.secondCommission.add(amount);
    }

    public void thridCommission(BigDecimal amount) {
        if (null == this.thridCommission) {
            this.thridCommission = BigDecimal.ZERO;
        }
        this.thridCommission = this.thridCommission.add(amount);
    }

    /**
     * 支出
     *
     * @param amount
     */
    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    /**
     * 冻结资金
     *
     * @param amount
     */
    public void freeze(BigDecimal amount) {
        this.freezeCapital = this.freezeCapital.add(amount);
    }

    /**
     * 解冻资金
     *
     * @param amount
     */
    public void unfreeze(BigDecimal amount) {
        this.freezeCapital = this.freezeCapital.subtract(amount);
    }

    /**
     * 获得可用余额
     *
     * @return
     */
    public BigDecimal getAvailableBalance() {
        return balance.subtract(freezeCapital);
    }

    /**
     * 检查可用余额是否足够
     *
     * @param amount
     * @return
     */
    public boolean isBalanceEnough(BigDecimal amount) {
        return this.getAvailableBalance().compareTo(amount) >= 0 ? true : false;
    }

    /**
     * 检查解冻资金金额是否足够
     *
     * @param amount
     * @return
     */
    public boolean isUnfreezeFundEnough(BigDecimal amount) {
        return this.freezeCapital.compareTo(amount) >= 0 ? true : false;
    }
}
