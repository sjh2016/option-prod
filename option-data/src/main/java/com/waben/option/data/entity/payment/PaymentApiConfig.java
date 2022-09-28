package com.waben.option.data.entity.payment;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.PaymentCashType;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 支付出入金api配置
 * 
 * <p>
 * 面向支付上游的配置，由程序员自行配置
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_payment_api_config")
public class PaymentApiConfig extends BaseEntity<Long> {

	/** 支付api名称 */
	private String name;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 商户ID */
	private String merchantId;
	/** 秘钥 */
	private String secretKey;
	/** 公钥 */
	private String publicKey;
	/** 私钥 */
	private String privateKey;
	/** 下单地址 */
	private String orderUrl;
	/** 支付地址 */
	private String payUrl;
	/** 查询地址 */
	private String queryUrl;
	/** 回调地址 */
	private String notifyUrl;
	/** 前端跳转地址 */
	private String returnUrl;
	/** 是否需要实名 */
	private Boolean needRealName;
	/** spring bean名称 */
	private String beanName;
	/** 是否可用 */
	private Boolean enable;
	/** 支付方式列表Json */
	private String methodJson;
	/** 支付方式列表 */
	@TableField(exist = false)
	private List<PaymentMehtod> methodList;

	@Data
	public static class PaymentMehtod {

		/** 支付方式ID */
		private Long id;
		/** 名称 */
		private String name;
		/** 支付参数 */
		private String param;
		/** 是否可用 */
		private Boolean enable;

	}

	public static final String NAME = "name";
	public static final String CASH_TYPE = "cash_type";
	public static final String MERCHANT_ID = "merchant_id";
	public static final String SECRET_KEY = "secret_key";
	public static final String PUBLIC_KEY = "public_key";
	public static final String PRIVATE_KEY = "private_key";
	public static final String ORDER_URL = "order_url";
	public static final String PAY_URL = "pay_url";
	public static final String QUERY_URL = "query_url";
	public static final String NOTIFY_URL = "notify_url";
	public static final String RETURN_URL = "return_url";
	public static final String NEED_REAL_NAME = "need_real_name";
	public static final String BEAN_NAME = "bean_name";
	public static final String ENABLE = "enable";
	public static final String METHOD_JSON = "method_json";

}
