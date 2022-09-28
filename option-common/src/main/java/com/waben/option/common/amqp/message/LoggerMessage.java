package com.waben.option.common.amqp.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoggerMessage {

    private Long userId;

    private String cmd;

    private String ip;

    private Map<String, Object> paramMap;

    private LocalDateTime time;

    private Integer errorCode;

}
