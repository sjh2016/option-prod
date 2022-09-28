package com.waben.option.mode.gateway.payment;

import java.math.BigDecimal;

import com.waben.option.common.model.enums.CurrencyEnum;

import lombok.Data;

@Data
public class GatewayPayPlaceOrderReq {

	/** 通道ID */
	private Long passagewayId;
	/** 请求支付币种数量 */
	private BigDecimal reqMoney;
	/** 请求支付币种 */
	private CurrencyEnum reqCurrency;
	/** 部分支付通道需要用户选择银行 */
	private String bankCode;
}
