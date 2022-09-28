package com.waben.option.common.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/6/24 16:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginMessage {

    private Long userId;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;
}
