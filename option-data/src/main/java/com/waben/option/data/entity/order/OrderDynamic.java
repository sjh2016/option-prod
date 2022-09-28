package com.waben.option.data.entity.order;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_u_order_dynamic", autoResultMap = true)
public class OrderDynamic extends BaseEntity<Long> {

	private Long userId;
	private String uid;
	private Long commodityId;
	private Integer cycle;
	private String name;
	private BigDecimal returnRate;
	private BigDecimal amount;

}
