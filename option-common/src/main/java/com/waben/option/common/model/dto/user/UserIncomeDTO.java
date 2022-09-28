package com.waben.option.common.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserIncomeDTO {

    @ApiModelProperty(value = "我的提成")
    private BigDecimal incomeTeam = BigDecimal.ZERO;

    @ApiModelProperty(value = "累计收益")
    private BigDecimal incomeGrand = BigDecimal.ZERO;

}
