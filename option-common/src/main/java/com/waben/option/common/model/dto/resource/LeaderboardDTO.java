package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.LeaderboardTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/6/23 16:06
 */
@Data
public class LeaderboardDTO {

    private Long id;

    private String name;

    private String headImg;

    private LeaderboardTypeEnum type;

    private BigDecimal power;

    private Integer workOrderCount;

    private LocalDateTime gmtCreate;
}
