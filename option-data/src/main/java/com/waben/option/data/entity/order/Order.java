package com.waben.option.data.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.common.model.enums.OrderStatusEnum;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/6/22 22:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_u_order")
public class Order extends BaseEntity<Long> {

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
	 * <li>1 个人贷款</li>
	 * <li>2 汽车贷款</li>
	 * <li>3 房地产贷款</li>
	 * </ul>
	 */
	private Integer type;

	private String specification;

	private BigDecimal returnRate;

	private BigDecimal income;

	private BigDecimal amount;

	private BigDecimal actualAmount;

	private OrderStatusEnum status;

	private BigDecimal perProfit;

	private BigDecimal profit;

	private String imgUrl;

	private BigDecimal volume;

	private BigDecimal panelVolume;

	private LocalDateTime auditTime;

	private String mobilePhone;

	private Boolean free;
	/**
	 * 是否需要退回本金
	 */
	private Boolean needReturn;

	public static final String USER_ID = "user_id";
	public static final String COMMODITY_ID = "commodity_id";
	public static final String NAME = "name";
	public static final String AMOUNT = "amount";
	public static final String ACTUAL_AMOUNT = "actual_amount";
	public static final String VOLUME = "volume";
	public static final String RETURN_RATE = "return_rate";
	public static final String STATUS = "status";
	public static final String AUDIT_TIME = "audit_time";
	public static final String PANEL_VOLUME = "panel_volume";
	public static final String FREE = "free";
	public static final String CYCLE = "cycle";
	public static final String WORKED_DAYS = "worked_days";
	public static final String NEED_RETURN = "need_return";
}
