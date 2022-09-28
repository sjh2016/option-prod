package com.waben.option.common.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderGroupSettlementMessage {

	/**
	 * 用户ID
	 */
	private Long userId;
	/**
	 * 用户组
	 */
	private Integer groupIndex;
	/**
	 * 父一级用户ID
	 */
	private Long parentId;
	/**
	 * 父二级用户ID
	 */
	private Long secondParentId;
	/**
	 * 父三级用户ID
	 */
	private Long thirdParentId;
	/**
	 * 给一级的收益分成比例
	 */
	private BigDecimal profitDivideRatio;
	/**
	 * 给二级的收益分成比例
	 */
	private BigDecimal secondProfitDivideRatio;
	/**
	 * 给二级的收益分成比例
	 */
	private BigDecimal thirdProfitDivideRatio;
	/**
	 * 订单结算信息列表
	 */
	private List<OrderSettlementInfo> orderInfoList;

	@Data
	public static class OrderSettlementInfo {

		/**
		 * 订单ID
		 */
		private Long id;
		/**
		 * 产品名称
		 */
		private String name;
		/**
		 * 产品金额
		 */
		private BigDecimal amount;
		/**
		 * 实际购买产品金额
		 */
		private BigDecimal actualAmount;
		/**
		 * 产品数量
		 */
		private BigDecimal volume;
		/**
		 * 日利率
		 */
		private BigDecimal returnRate;
		/**
		 * 收益金额
		 */
		private BigDecimal profit;
		/**
		 * 是否需要给上级分成
		 */
		private boolean needDivide;
		/**
		 * 给一级的分成金额
		 */
		private BigDecimal profitDivide;
		/**
		 * 给二级的分成金额
		 */
		private BigDecimal secondProfitDivide;
		/**
		 * 给三级的分成金额
		 */
		private BigDecimal thirdProfitDivide;
		/**
		 * 周期
		 */
		private Integer cycle;
		/**
		 * 已产生收益的天数
		 */
		private Integer workedDays;
		/**
		 * 是否需要退回本金
		 */
		private Boolean needReturn;

	}

}
