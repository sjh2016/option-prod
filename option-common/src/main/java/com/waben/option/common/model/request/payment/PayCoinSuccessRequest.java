package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayCoinSuccessRequest {

	/** 用户ID */
	private Long userId;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 实际支付币种数量 */
	private BigDecimal realMoney;
	/** 支付币种 */
	private CurrencyEnum reqCurrency;
	/** 支付apiID */
	private Long payApiId;
	/** 支付api名称 */
	private String payApiName;
	/** 支付方式ID */
	private Long payMethodId;
	/** 支付方式名称 */
	private String payMethodName;
	/** 钱包地址（充币才有值） */
	private String burseAddress;
	/** 钱包类型（充币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 交易hash（充币才有值） */
	private String hash;

}
