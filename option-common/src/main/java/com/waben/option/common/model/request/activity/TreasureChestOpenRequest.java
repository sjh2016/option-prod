package com.waben.option.common.model.request.activity;

import io.swagger.annotations.ApiParam;
import lombok.Data;

@Data
public class TreasureChestOpenRequest {

	/** 用户ID */
	@ApiParam(hidden = true)
	private Long userId;
	/** 密码 */
	private String password;

}
