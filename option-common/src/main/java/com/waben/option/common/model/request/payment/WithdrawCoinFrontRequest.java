package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawCoinFrontRequest {

	/** 请求提现数量 */
	private BigDecimal reqNum;
	/** 提现币种 */
	private CurrencyEnum reqCurrency;
	/** 钱包地址 */
	private String burseAddress;
	/** 钱包类型（充币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 资金密码 */
	private String fundPassword;

}
