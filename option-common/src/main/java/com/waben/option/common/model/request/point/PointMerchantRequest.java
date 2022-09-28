package com.waben.option.common.model.request.point;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PointMerchantRequest {

	/** 主键ID */
	private Long id;
	/** 商家名称 */
	private String name;
	/** 商家logo */
	private String logo;
	/** 最小金额 */
	private BigDecimal minAmount;
	/** 最大金额 */
	private BigDecimal maxAmount;
	/** 单跑收益点 */
	private BigDecimal runPoint;
	/** usdt单价 */
	private BigDecimal usdtPrice;
	/** 成交率 */
	private BigDecimal turnoverRate;
	/** 最大可购买总额 */
	private BigDecimal limitAmount;
	/** 已购买金额 */
	private BigDecimal usedAmount;
	/** 是否上线 */
	private Boolean online;
	/** 排序 */
	private Integer sort;

}
