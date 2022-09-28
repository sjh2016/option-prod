package com.waben.option.common.model.dto.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.waben.option.common.model.enums.ProductOrderStatusEnum;

import lombok.Data;

@Data
public class PointProductOrderDTO {

	/** 主键ID */
	private Long id;
	/** 用户ID */
	private Long userId;
	/** 产品ID */
	private Long productId;
	/** 产品名称 */
	private String productName;
	/** 星级 */
	private Integer starLevel;
	/** 订单金额 */
	private BigDecimal amount;
	/** 实际支付金额 */
	private BigDecimal actualAmount;
	/** 每天刷新可跑的次数 */
	private Integer runRefreshQuantity;
	/** 当天可跑次数 */
	private Integer runTotalQuantity;
	/** 当天已跑次数 */
	private Integer runUsedQuantity;
	/** 最小单跑收益点 */
	private BigDecimal minRunPoint;
	/** 最大单跑收益点 */
	private BigDecimal maxRunPoint;
	/** 总收益 */
	private BigDecimal totalProfit;
	/** 状态 */
	private ProductOrderStatusEnum status;
	/** 审核时间 */
	private LocalDateTime auditTime;
	/** 创建时间 */
	private LocalDateTime gmtCreate;

}
