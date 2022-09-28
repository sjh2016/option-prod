package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/29 18:37
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "t_d_level", autoResultMap = true)
public class Level extends BaseEntity<Long> {

    private BigDecimal fee;

    private Integer level;

    private BigDecimal amount;

    private BigDecimal limitAmount;

    public static final String AMOUNT = "amount";

    public static final String LEVEL = "level";
}
