package com.waben.option.data.entity.simulate;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_data_uid", autoResultMap = true)
public class UidData extends BaseEntity<Long> {

	private String uid;

	private BigDecimal investAmount;

	private BigDecimal withdrawalAmount;
	
	private BigDecimal paymentAmount;
	
	public static final String INVEST_AMOUNT = "invest_amount";

}
