package com.waben.option.mode.gateway.payment;

import java.math.BigDecimal;

import com.waben.option.common.model.enums.CurrencyEnum;

import lombok.Data;

@Data
public class GatewayWithdrawPlaceOrderReq {

	/** 请求提现金额 */
	private BigDecimal reqNum;
	/** 到账币种 */
	private CurrencyEnum targetCurrency;
	/** 通道ID */
	private Long passagewayId;
	/** 绑卡ID */
	private Long bindId;

}
