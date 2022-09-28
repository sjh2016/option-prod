package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayFrontRequest {
	
	/** 通道ID */
	private Long passagewayId;
	/** 请求支付币种数量 */
	private BigDecimal reqMoney;
	/** 请求支付币种 */
	private CurrencyEnum reqCurrency;
	
	/** 请求到账币种数量 */
	private BigDecimal reqNum;
	/** 汇率 */
	private BigDecimal exchangeRate;
	
	/** 部分支付通道需要用户选择银行 */
	private String bankCode;
	
}
