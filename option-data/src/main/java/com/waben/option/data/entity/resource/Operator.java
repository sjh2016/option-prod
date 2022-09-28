package com.waben.option.data.entity.resource;

import com.baomidou.mybatisplus.annotation.TableName;
import com.waben.option.data.entity.BaseTemplateEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(value = "t_d_operator", autoResultMap = true)
public class Operator extends BaseTemplateEntity {

    private String operator;

    private Integer countryId;

    public static final String OPERATOR = "operator";

    public static final String COUNTRY_ID = "country_id";

}
