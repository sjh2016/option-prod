package com.waben.option.common.model.request.order;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/12 17:10
 */
@Data
public class PaymentOrderRequest {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 请求IP
     */
    private String ip;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 运营商ID
     */
    private Long operatorId;

    /**
     * 通道ID
     */
    private Long passagewayId;

    /**
     * 手机区号
     */
    private String areaCode;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 支付币种
     */
    private CurrencyEnum currency;

    /**
     * 邮箱地址
     */
    private String email;
}
