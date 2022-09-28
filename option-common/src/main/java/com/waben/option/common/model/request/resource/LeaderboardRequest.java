package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/6/23 15:59
 */
@Data
public class LeaderboardRequest {

    private String name;

    private String headImg;

    private LeaderboardTypeEnum type;

    private BigDecimal power;

    private Integer workOrderCount;
}
