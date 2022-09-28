package com.waben.option.common.model.dto.resource;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class LuckyDrawCommodityDTO {

    private Long id;

    private Integer number;

    private BigDecimal amount;

    private String currency;

    private String unit;

    private Integer sort;

    private LocalDateTime gmtCreate;
}
