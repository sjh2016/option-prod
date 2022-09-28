package com.waben.option.data.entity.payment;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 支付通道
 * 
 * <p>
 * 面向业务的通道配置，由运营人员在运营后台配置
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_payment_passageway")
public class PaymentPassageway extends BaseEntity<Long> {

	/** 管理后台显示名称 */
	private String displayName;
	/** 支付apiID */
	private Long payApiId;
	/** 支付方式ID */
	private Long payMethodId;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 最小限额 */
	private BigDecimal minAmount;
	/** vip最小限额 */
	private BigDecimal vipMinAmount;
	/** 最大限额 */
	private BigDecimal maxAmount;
	/** 通道logo */
	private String logo;
	/** 排序（数字大在后，小在前） */
	private Integer sort;
	/** 是否上线 */
	private Boolean enable;
	/** 是否需要kyc信息 */
	private Boolean needKyc;
	/** 汇率列表Json */
	private String exchangeRateJson;
	/** 多语言列表Json */
	private String languageJson;
	/** 支持的国家（多个用英文逗号分隔） */
	private String country;
	/** 充值选项 */
	private String selection;

	public static final String PAY_API_ID = "pay_api_id";
	public static final String PAY_METHOD_ID = "pay_method_id";
	public static final String CASH_TYPE = "cash_type";
	public static final String EXCHANGE_RATE = "exchange_rate";
	public static final String MIN_AMOUNT = "min_amount";
	public static final String MAX_AMOUNT = "max_amount";
	public static final String LOGO = "logo";
	public static final String SORT = "sort";
	public static final String ENABLE = "enable";
	public static final String NEED_KYC = "need_kyc";
	public static final String EXCHANGE_RATE_JSON = "exchange_rate_json";
	public static final String LANGUAGE_JSON = "language_json";
	public static final String COUNTRY = "country";

}
