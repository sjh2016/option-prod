package com.waben.option.common.model.request.order;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderRequest {

	private Long commodityId;

	@ApiModelProperty(hidden = true)
	private Long userId;

	private BigDecimal volume;

	@ApiModelProperty(hidden = true)
	private Boolean giftLogo;

}
