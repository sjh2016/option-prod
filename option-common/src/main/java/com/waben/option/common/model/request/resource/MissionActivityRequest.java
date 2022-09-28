package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MissionActivityRequest {

    private Long id;

    private String name;

    private ActivityTypeEnum type;

    private BigDecimal amount;

    private Integer minLimitNumber;

    private Integer maxLimitNumber;

    private LocalDateTime expirationDate;

    private String description;

    private Integer sort;

    private Boolean enable;

}
