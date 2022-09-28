package com.waben.option.common.model.request.payment;

import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO.ExchangeRateDTO;
import com.waben.option.common.model.dto.payment.PaymentPassagewayDTO.PaymentPassagewayLanguageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentPassagewayRequest {

	/** 主键ID */
	private Long id;
	/** 前端显示名称 */
	private String displayName;
	/** 前端显示名称(英文) */
	private String displayNameEnglish;
	/** 支付apiID */
	private Long payApiId;
	/** 支付方式ID */
	private Long payMethodId;
	/** 最小限额 */
	private BigDecimal minAmount;
	/** 最大限额 */
	private BigDecimal maxAmount;
	/** 通道logo */
	private String logo;
	/** 是否需要kyc信息 */
	private Boolean needKyc;
	/** 充值选项 */
	private String selection;
	/** 汇率列表 */
	private List<ExchangeRateDTO> exchangeRateList;
	/** 多语言列表 */
	private List<PaymentPassagewayLanguageDTO> languageList;
	/** 支持的国家（多个用英文逗号分隔） */
	private String country;

}
