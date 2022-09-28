package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.ActivityTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author: Peter
 * @date: 2021/7/16 17:34
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class UserMissionRequest {

    private Long userId;

    private BigDecimal finishCount = BigDecimal.ONE;

    private ActivityTypeEnum activityType;
}
