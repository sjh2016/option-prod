package com.waben.option.data.entity.activity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_d_allowance")
public class Allowance extends BaseTemplateEntity {

	private BigDecimal total;

	private BigDecimal distributed;

}
