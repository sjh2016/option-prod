package com.waben.option.common.model.dto.payment;

import com.waben.option.common.model.dto.payment.PaymentApiConfigDTO.PaymentMethodDTO;
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
public class PaymentApiConfigSimpleDTO {

	/** 主键ID */
	private Long id;
	/** 支付api名称 */
	private String name;
	/** 出入金类型 */
	private PaymentCashType cashType;
	/** 是否可用 */
	private Boolean enable;
	/** 支付方式列表 */
	private List<PaymentMethodDTO> methodList;

}
