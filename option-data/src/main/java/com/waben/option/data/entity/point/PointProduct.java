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
@TableName(value = "t_d_point_product", autoResultMap = true)
public class PointProduct extends BaseEntity<Long> {

	/** 产品名称 */
	private String name;
	/** 星级 */
	private Integer starLevel;
	/** 金额 */
	private BigDecimal amount;
	/** 实际需支付金额 */
	private BigDecimal actualAmount;
	/** 周期（天） */
	private Integer cycle;
	/** 最小单跑收益点 */
	private BigDecimal minRunPoint;
	/** 最大单跑收益点 */
	private BigDecimal maxRunPoint;
	/** 购买一次每天可跑次数 */
	private Integer runQuantity;
	/** 最大可购买总数 */
	private Integer limitQuantity;
	/** 已购买次数 */
	private Integer usedQuantity;
	/** 是否赠品（赠品不允许用户下单） */
	private Boolean gift;
	/** 是否上线 */
	private Boolean online;
	/** 排序 */
	private Integer sort;

	public static final String ONLINE = "online";
	public static final String GIFT = "gift";
	public static final String SORT = "sort";

}
