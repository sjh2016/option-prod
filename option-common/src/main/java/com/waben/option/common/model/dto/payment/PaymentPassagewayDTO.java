package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.enums.PaymentCashType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付通道
 * 
 * <p>
 * 面向业务的通道配置，由运营人员在运营后台配置
 * </p>
 */
@Data
public class PaymentPassagewayDTO {

	/** 主键ID */
	private Long id;
	/** 前端显示名称 */
	private String displayName;
	/** 描述 */
	private String description;
	/** 支付apiID */
	private Long payApiId;
	/** 支付api名称 */
	private String payApiName;
	/** 支付方式ID */
	private Long payMethodId;
	/** 支付方式名称 */
	private String payMethodName;
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
	/** 充值选项 */
	private String selection;
	/** 创建时间 */
	private LocalDateTime gmtCreate;
	/** 汇率列表 */
	private List<ExchangeRateDTO> exchangeRateList;
	/** 多语言列表 */
	private List<PaymentPassagewayLanguageDTO> languageList;
	/** 支持的国家（多个用英文逗号分隔） */
	private String country;

	public String getLanguageDisplayName(String language) {
		String result = "";
		if (languageList != null && languageList.size() > 0) {
			for (PaymentPassagewayLanguageDTO dto : languageList) {
				if (language.equalsIgnoreCase(dto.getLanguage())) {
					return dto.getDisplayName();
				}
			}
		}
		return result;
	}

	public String getLanguageDescription(String language) {
		String result = "";
		if (languageList != null && languageList.size() > 0) {
			for (PaymentPassagewayLanguageDTO dto : languageList) {
				if (language.equalsIgnoreCase(dto.getLanguage())) {
					return dto.getDescription();
				}
			}
		}
		return result;
	}

	@Data
	public static class ExchangeRateDTO {

		/** 货币 */
		private String currency;
		/** 汇率 */
		private BigDecimal exchangeRate;
		/** 货币符号 */
		private String symbol;
		/** 手续费率 **/
		private BigDecimal feeRate;
		/** 个税费率 */
		private BigDecimal taxRate;
		/** vip手续费率 **/
		private BigDecimal vipFeeRate;
		/** vip个税费率 */
		private BigDecimal vipTaxRate;

	}

	@Data
	public static class PaymentPassagewayLanguageDTO {

		/** 语言 */
		private String language;
		/** 前端显示名称 */
		private String displayName;
		/** 描述 */
		private String description;

	}

}
