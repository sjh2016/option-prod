package com.waben.option.common.model.dto.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.waben.option.common.model.enums.RunOrderStatusEnum;

import lombok.Data;

@Data
public class PointRunOrderDynamicDTO {

	/** 主键ID */
	private Long id;
	/** UID */
	private String uid;
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

}
