package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.FeeMethodType;
import com.waben.option.common.model.enums.PaymentCashType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentFeeConfigRequest {

	/** 主键ID */
	private Long id;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 币种 */
	private CurrencyEnum currency;
	/** 钱包类型，如ERC20、TRC20 */
	private String burseType;
	/** 支付apiID */
	private Long payApiId;
	/** 手续费收取方式 */
	private FeeMethodType feeType;
	/** 手续费参数值 */
	private BigDecimal fee;
	
	

}
