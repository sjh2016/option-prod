package com.waben.option.common.model.dto.order;

import com.waben.option.common.model.enums.OrderStatusEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/6/23 22:27
 */
@Data
public class OrderDTO {

	private Long id;

	private Long userId;

	private String randomId;

	private Long commodityId;
	/**
	 * 周期
	 */
	private Integer cycle;
    /**
     * 已产生收益的天数
     */
    private Integer workedDays;

	private String name;

	/**
	 * 类型
	 * <ul>
	 * <li>1 移动设备</li>
	 * <li>2 电动汽车</li>
	 * </ul>
	 */
	private Integer type;

	private String specification;

	private BigDecimal returnRate;

	private BigDecimal income;

	private BigDecimal amount;

	private BigDecimal actualAmount;

	private OrderStatusEnum status;

	private String imgUrl;

	private BigDecimal perProfit = BigDecimal.ZERO;

	private BigDecimal profit = BigDecimal.ZERO;

	private Integer workDay = 0;

	private BigDecimal volume;

	private BigDecimal panelVolume;

	private LocalDateTime auditTime;

	private LocalDateTime gmtCreate;

	private String mobilePhone;

	private Boolean free;
}
