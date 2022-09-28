package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_lucky_draw_commodity", autoResultMap = true)
public class LuckyDrawCommodity extends BaseEntity<Long> {

    private Integer number;

    private BigDecimal amount;

    private String currency;

    private String unit;

    private Integer sort;
}
