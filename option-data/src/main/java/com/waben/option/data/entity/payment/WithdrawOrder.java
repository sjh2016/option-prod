package com.waben.option.data.entity.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.CurrencyEnum;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.common.model.enums.WithdrawOrderStatusEnum;
import com.waben.option.common.model.enums.WithdrawTypeEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 提现订单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_withdraw_order")
public class WithdrawOrder extends BaseEntity<Long> {

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
	/** 上游返回信息 */
	private String thirdRespMsg;
	/** 订单状态 */
	private WithdrawOrderStatusEnum status;
	/** 提现类型 */
	private WithdrawTypeEnum type;
	/** 请求提现数量 */
	private BigDecimal reqNum;
	/** 请求提现金额 */
	private BigDecimal reqMoney;
	/** 提现到账数量（扣除手续费） */
	private BigDecimal realNum;
	/** 提现币种 */
	private CurrencyEnum reqCurrency;
	/** 到账币种 */
	private CurrencyEnum targetCurrency;
	/** 手续费 */
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
	/** 钱包地址（提币才有值） */
	private String burseAddress;
	/** 钱包类型（提币才有值），如ERC20、TRC20 */
	private String burseType;
	/** 交易hash（提币才有值） */
	private String hash;
	/** 银行卡号（OTC出售才有值） */
	private String bankCardId;
	/** 姓名（OTC出售才有值） */
	private String name;
	/** 手机号码（OTC出售才有值） */
	private String mobilePhone;
	/** 身份证号（OTC出售才有值） */
	private String idCard;
	/** 所属银行名称（OTC出售才有值） */
	private String bankName;
	/** 所属银行代码（OTC出售才有值） */
	private String bankCode;
	/** 省份名称（OTC出售才有值） */
	private String provinceName;
	/** 城市名称（OTC出售才有值） */
	private String cityName;
	/** 所属支行名称（OTC出售才有值） */
	private String branchName;
	/** 备注 */
	private String remark;
	/** 是否提现红包收益 */
	private Boolean isLuckyProfit;
	/** 到账时间 */
	private LocalDateTime arrivalTime;
	/** 审核时间 */
	private LocalDateTime auditTime;
	/** 审核人员ID */
	private Long auditUserId;
	/** 审核人用户名 */
	private String auditUsername;

	public static final String USER_ID = "user_id";
	public static final String BROKER_SYMBOL = "broker_symbol";
	public static final String CASH_TYPE = "cash_type";
	public static final String ORDER_NO = "order_no";
	public static final String THIRD_ORDER_NO = "third_order_no";
	public static final String THIRD_RESP_MSG = "third_resp_msg";
	public static final String STATUS = "status";
	public static final String TYPE = "type";
	public static final String REQ_NUM = "req_num";
	public static final String REQ_MONEY = "req_money";
	public static final String REAL_NUM = "real_num";
	public static final String REQ_CURRENCY = "req_currency";
	public static final String TARGET_CURRENCY = "target_currency";
	public static final String FEE = "fee";
	public static final String PAY_API_ID = "pay_api_id";
	public static final String PAY_API_NAME = "pay_api_name";
	public static final String PAY_METHOD_ID = "pay_method_id";
	public static final String PASSAGEWAY_ID = "passageway_id";
	public static final String EXCHANGE_RATE = "exchange_rate";
	public static final String BURSE_ADDRESS = "burse_address";
	public static final String HASH = "hash";
	public static final String BANK_CARD_ID = "bank_card_id";
	public static final String NAME = "name";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String ID_CARD = "id_card";
	public static final String BANK_NAME = "bank_name";
	public static final String BANK_CODE = "bank_code";
	public static final String PROVINCE_NAME = "province_name";
	public static final String CITY_NAME = "city_name";
	public static final String BRANCH_NAME = "branch_name";
	public static final String REMARK = "remark";
	public static final String IS_LUCKY_PROFIT = "is_lucky_profit";
	public static final String ARRIVAL_TIME = "arrival_time";
	public static final String AUDIT_USER_ID = "audit_user_id";
	public static final String AUDIT_USERNAME = "audit_username";

}
