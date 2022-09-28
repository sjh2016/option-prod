package com.waben.option.data.entity.point;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_point_merchant", autoResultMap = true)
public class PointMerchant extends BaseEntity<Long> {

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

	public static final String MIN_AMOUNT = "min_amount";
	public static final String MAX_AMOUNT = "max_amount";
	public static final String ONLINE = "online";
	public static final String SORT = "sort";

}
