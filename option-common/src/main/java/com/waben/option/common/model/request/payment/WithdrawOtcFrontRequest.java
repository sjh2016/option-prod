package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawOtcFrontRequest {

	/** 请求提现金额 */
	private BigDecimal reqNum;
	/** 到账币种 */
	private CurrencyEnum targetCurrency;
	/** 通道ID */
	private Long passagewayId;
	/** 绑卡ID */
	private Long bindId;
	/** 数字钱包地址 */
	private String burseAddress;
	/** 资金密码 */
	private String fundPassword;

}
