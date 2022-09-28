package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单
 */
@Data
public class PaymentOrderDTO {

	/** 主键ID */
	private Long id;
	/** 用户ID */
	private Long userId;
	/** 代理symbol */
	private String brokerSymbol;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 订单编号 */
	private String orderNo;
	/** 上游订单编号 */
	private String thirdOrderNo;
	/** 订单状态 */
	private PaymentOrderStatusEnum status;
	/** 请求到账币种数量 */
	private BigDecimal reqNum;
	/** 请求支付币种数量 */
	private BigDecimal reqMoney;
	/** 实际支付币种数量 */
	private BigDecimal realMoney;
	/** 实际到账币种数量（扣着手续费后） */
	private BigDecimal realNum;
	/** 支付币种 */
	private CurrencyEnum reqCurrency;
	/** 到账币种 */
	private CurrencyEnum targetCurrency;
	/** 手续费（支付币种） */
	private BigDecimal fee;
	/** 支付apiID */
	private Long payApiId;
	/** 支付api名称 */
	private String payApiName;
	/** 支付方式ID */
	private Long payMethodId;
	/** 支付方式名称 */
	private String payMethodName;
	/** 支付通道ID */
	private Long passagewayId;
	/** 汇率 */
	private BigDecimal exchangeRate;
	/** 钱包地址（充币才有值） */
	private String burseAddress;
	/** 钱包类型（充币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 交易hash（充币才有值） */
	private String hash;
	/** 支付html */
	private String payHtml;
	/** 获得的红包金额之和 */
	private BigDecimal luckyMoney;
	/** 到账时间 */
	private LocalDateTime arrivalTime;
	/** 创建时间 */
	private LocalDateTime gmtCreate;

}
