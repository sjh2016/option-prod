package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import lombok.Data;

import java.util.List;

/**
 * 支付出入金api配置
 * 
 * <p>
 * 面向支付上游的配置，由程序员自行配置
 * </p>
 */
@Data
public class PaymentApiConfigDTO {

	/** 主键ID */
	private Long id;
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
	/** 支付方式列表 */
	private List<PaymentMethodDTO> methodList;

	@Data
	public static class PaymentMethodDTO {

		/** 支付方式ID */
		private Long id;
		/** 名称 */
		private String name;
		/** 支付参数 */
		private String param;
		/** 是否可用 */
		private Boolean enable;

	}

}
