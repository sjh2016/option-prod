package com.waben.option.data.entity.activity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_allowance_detail")
public class AllowanceDetail extends BaseEntity<Long> {

	private Long orderId;
	
	private Long userId;

	private Integer cycle;

	private BigDecimal returnRate;

	private BigDecimal amount;
	/** 整个周期收益的80% */
	private BigDecimal distributed;
	/**
     * 类型
     * <ul>
     * <li>1 个人贷款</li>
     * <li>2 汽车贷款</li>
     * <li>3 房地产贷款</li>
     * </ul>
     */
    private Integer type;

}
