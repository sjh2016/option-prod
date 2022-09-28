package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值选项和赠送设置
 */
@Data
public class RechargeConfigDTO {

	/**
	 * 充值选项列表
	 */
	private List<BigDecimal> itemList;

	/**
	 * 充值赠送配置列表
	 */
	private List<RechargeGiveDTO> rechargeGiveList;

	/**
	 * 赠送配置
	 */
	@Data
	public static class RechargeGiveDTO {

		/**
		 * 充值金额，条件 >= 该值，触发赠送
		 */
		private BigDecimal amount;

		/**
		 * 赠送比例，如20%，该值为20
		 */
		private BigDecimal giveRatio;

	}

}
