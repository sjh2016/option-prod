package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MissionActivityDTO {

    private Long id;

    private String name;

    private ActivityTypeEnum type;

    private BigDecimal amount;

    private Integer minLimitNumber;

    private Integer maxLimitNumber;

    private String description;

    private Integer sort;

    private Boolean enable;

    private Boolean daily;

    private Boolean awardCreate;

    private Boolean limitInviteVolume;
    
    private BigDecimal stepInvestment;

    private LocalDateTime gmtCreate;

}
