package com.waben.option.common.model.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawAdminStaDTO {

    /**
     * 成功充值笔数
     */
    private long successCount;
    /**
     * 请求提现数量总和
     */
    private BigDecimal reqNumTotal;
    /**
     * 提现到账数量（扣除手续费）总和
     */
    private BigDecimal realNumTotal;

}
