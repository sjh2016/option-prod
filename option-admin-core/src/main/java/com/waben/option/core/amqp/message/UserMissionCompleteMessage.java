package com.waben.option.core.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMissionCompleteMessage {

    private Long userId;

    private Long completeId;

    private BigDecimal inviteVolume;

    private BigDecimal amount;

    private BigDecimal minLimitNumber;
}
