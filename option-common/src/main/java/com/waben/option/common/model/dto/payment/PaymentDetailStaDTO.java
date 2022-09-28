package com.waben.option.common.model.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDetailStaDTO {

	private String platform;
	
	private BigDecimal amount;

}
