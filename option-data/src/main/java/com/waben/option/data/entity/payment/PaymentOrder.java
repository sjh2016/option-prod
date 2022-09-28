package com.waben.option.data.entity.payment;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.PaymentOrderStatusEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值订单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_payment_order")
public class PaymentOrder extends BaseEntity<Long> {

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
	/** 到账时间 */
	private LocalDateTime arrivalTime;
	/** 设备类型 */
	private String platform;
	/** 获得的红包金额之和 */
	private BigDecimal luckyMoney;
	private Boolean isHidden;


	/** 请求到账币种数量 */
	@TableField(value = "ifnull(sum(req_num),0)",
			insertStrategy = FieldStrategy.NEVER,
			updateStrategy = FieldStrategy.NEVER,
			select = false)
	private BigDecimal reqNumSum;
	/** 请求支付币种数量 */
	@TableField(value = "ifnull(sum(req_money),0)",
			insertStrategy = FieldStrategy.NEVER,
			updateStrategy = FieldStrategy.NEVER,
			select = false)
	private BigDecimal reqMoneySum;
	/** 实际支付币种数量 */
	@TableField(value = "ifnull(sum(real_money),0)",
			insertStrategy = FieldStrategy.NEVER,
			updateStrategy = FieldStrategy.NEVER,
			select = false)
	private BigDecimal realMoneySum;
	/** 实际到账币种数量（扣着手续费后） */
	@TableField(value = "ifnull(sum(real_num),0)",
			insertStrategy = FieldStrategy.NEVER,
			updateStrategy = FieldStrategy.NEVER,
			select = false)
	private BigDecimal realNumSum;


	public static final String USER_ID = "user_id";
	public static final String BROKER_SYMBOL = "broker_symbol";
	public static final String CASH_TYPE = "cash_type";
	public static final String ORDER_NO = "order_no";
	public static final String THIRD_ORDER_NO = "third_order_no";
	public static final String STATUS = "status";
	public static final String REQ_NUM = "req_num";
	public static final String REQ_MONEY = "req_money";
	public static final String REAL_MONEY = "real_money";
	public static final String REAL_NUM = "real_num";
	public static final String REQ_CURRENCY = "req_currency";
	public static final String TARGET_CURRENCY = "target_currency";
	public static final String FEE = "fee";
	public static final String PAY_API_ID = "pay_api_id";
	public static final String PAY_API_NAME = "pay_api_name";
	public static final String PAY_METHOD_ID = "pay_method_id";
	public static final String PAY_METHOD_NAME = "pay_method_name"; 
	public static final String PASSAGEWAY_ID = "passageway_id";
	public static final String EXCHANGE_RATE = "exchange_rate";
	public static final String BURSE_ADDRESS = "burse_address";
	public static final String HASH = "hash";
	public static final String PAY_HTML = "pay_html";
	public static final String ARRIVAL_TIME = "arrival_time";
	public static final String PLATFORM = "platform";
	public static final String LUCKY_MONEY = "lucky_money";
	public static final String IS_HIDDEN = "is_hidden";
	

}
