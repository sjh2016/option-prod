package com.waben.option.data.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.FeeMethodType;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 出入金手续费配置
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_payment_fee_config")
public class PaymentFeeConfig extends BaseEntity<Long> {

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
	
	public static final String CASH_TYPE = "cash_type";
	public static final String CURRENCY = "currency";
	public static final String PAY_API_ID = "pay_api_id";
	public static final String FEE_TYPE = "fee_type";
	public static final String BURSE_TYPE = "burse_type";
	public static final String FEE = "fee";

}
