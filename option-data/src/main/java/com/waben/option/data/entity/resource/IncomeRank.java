package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_d_income_rank")
public class IncomeRank extends BaseEntity<Long> {

    private String name;

    private BigDecimal amount;

    private Integer inviteNumber;

    private BigDecimal income;

    private String type;

    private String headImg;

    public static final String AMOUNT = "amount";

    public static final String INCOME = "income";

    public static final String TYPE = "type";

}
