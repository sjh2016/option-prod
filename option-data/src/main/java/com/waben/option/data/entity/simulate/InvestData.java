package com.waben.option.data.entity.simulate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_data_invest", autoResultMap = true)
public class InvestData extends BaseEntity<Long> {

	private Long userId;
	
	private String uid;
	
	private BigDecimal amount;
	
	private LocalDateTime time;
	
}
