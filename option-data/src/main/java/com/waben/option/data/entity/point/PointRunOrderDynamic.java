package com.waben.option.data.entity.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.RunOrderStatusEnum;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_u_point_run_order_dynamic", autoResultMap = true)
public class PointRunOrderDynamic extends BaseEntity<Long> {

	/** 用户ID */
	private Long userId;
	/** UID */
	private String uid;
	/** 订单ID */
	private Long productOrderId;
	/** 产品ID */
	private Long productId;
	/** 产品名称 */
	private String productName;
	/** 星级 */
	private Integer starLevel;
	/** 金额 */
	private BigDecimal amount;
	/** 商户ID */
	private Long merchantId;
	/** 商户名称 */
	private String merchantName;
	/** 单跑收益点 */
	private BigDecimal runPoint;
	/** 最小单跑收益点 */
	private BigDecimal minRunPoint;
	/** 最大单跑收益点 */
	private BigDecimal maxRunPoint;
	/** 收益 */
	private BigDecimal profit;
	/** 状态 */
	private RunOrderStatusEnum status;
	/** 请求跑时间 */
	private LocalDateTime runTime;
	/** 最终态时间（成功或者失败时间） */
	private LocalDateTime finalTime;
	/** 到期时间 */
	private LocalDateTime expireTime;

	public static final String USER_ID = "user_id";
	public static final String AMOUNT = "amount";
	public static final String STATUS = "status";
	public static final String RUN_TIME = "run_time";
	public static final String EXPIRE_TIME = "expire_time";

}
