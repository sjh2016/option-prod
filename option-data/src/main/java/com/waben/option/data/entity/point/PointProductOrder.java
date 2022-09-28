package com.waben.option.data.entity.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.ProductOrderStatusEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_u_point_product_order", autoResultMap = true)
public class PointProductOrder extends BaseEntity<Long> {

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
	/** 周期（天） */
	private Integer cycle;
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
	/** 是否赠品 */
	private Boolean gift;
	/** 状态 */
	private ProductOrderStatusEnum status;
	/** 审核时间 */
	private LocalDateTime auditTime;

	public static final String PRODUCT_ID = "product_id";
	public static final String USER_ID = "user_id";
	public static final String GIFT = "gift";
	public static final String STATUS = "status";

}
