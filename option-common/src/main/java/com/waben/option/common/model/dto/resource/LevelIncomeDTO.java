package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LevelIncomeDTO {

    /**
     * 下级等级
     */
    private int level;

    /**
     * 收益分成比例
     */
    private BigDecimal income;
    
    /**
     * 投资分成比例
     */
    private BigDecimal investment;
    
}
