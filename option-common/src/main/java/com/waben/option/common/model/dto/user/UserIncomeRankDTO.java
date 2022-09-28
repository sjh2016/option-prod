package com.waben.option.common.model.dto.user;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserIncomeRankDTO {

    @ApiModelProperty(value = "我的收益")
    private BigDecimal amount;

    @ApiModelProperty(value = "我的排名")
    private int rankNo;

    @ApiModelProperty(value = "用户id")
    private Long userId;

}
