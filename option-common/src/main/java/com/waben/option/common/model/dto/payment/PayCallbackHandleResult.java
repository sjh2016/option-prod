package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentCashType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayCallbackHandleResult {

	/** 是否成功 */
	private boolean isPaySuccess;
	/** 返回上游的数据 */
	private String backThirdData;

	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 订单编号 */
	private String orderNo;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 实际支付币种数量 */
	private BigDecimal realMoney;
	
	/** 支付币种 */
	private CurrencyEnum reqCurrency;
	/** 用户ID */
	private Long userId;
	/** 钱包地址（充币才有值） */
	private String burseAddress;
	/** 钱包类型（充币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 交易hash */
	private String hash;

	public PayCallbackHandleResult() {
	}

	public PayCallbackHandleResult(boolean isPaySuccess, String backThirdData) {
		super();
		this.isPaySuccess = isPaySuccess;
		this.backThirdData = backThirdData;
	}

}
