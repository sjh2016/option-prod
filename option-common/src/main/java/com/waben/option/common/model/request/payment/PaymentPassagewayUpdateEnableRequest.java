package com.waben.option.common.model.request.payment;

import lombok.Data;

@Data
public class PaymentPassagewayUpdateEnableRequest {

	/** 主键ID */
	private Long id;
	/** 是否上线 */
	private Boolean enable;

}	
