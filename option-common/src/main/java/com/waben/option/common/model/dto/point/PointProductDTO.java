package com.waben.option.common.model.dto.point;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PointProductDTO {

	/** 主键ID */
	private Long id;
	/** 产品名称 */
	private String name;
	/** 星级 */
	private Integer starLevel;
	/** 金额 */
	private BigDecimal amount;
	/** 实际需支付金额 */
	private BigDecimal actualAmount;
	/** 购买一次每天可跑次数 */
	private BigDecimal runQuantity;
	/** 周期（天） */
	private Integer cycle;
	/** 最小单跑收益点 */
	private BigDecimal minRunPoint;
	/** 最大单跑收益点 */
	private BigDecimal maxRunPoint;
	/** 最大可购买总数 */
	private Integer limitQuantity;
	/** 已购买次数 */
	private Integer usedQuantity;
	/** 是否上线 */
	private Boolean online;
	/** 排序 */
	private Integer sort;

}
