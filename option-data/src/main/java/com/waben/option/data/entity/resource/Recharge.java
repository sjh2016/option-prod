package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_d_recharge", autoResultMap = true)
public class Recharge extends BaseTemplateEntity {

    private Integer operatorId;

    private BigDecimal amount;

    private BigDecimal serviceCharge;

    private String currency;

    public static final String OPERATOR_ID = "operator_id";

    public static final String AMOUNT = "amount";

}
