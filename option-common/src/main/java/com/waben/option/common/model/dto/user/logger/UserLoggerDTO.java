package com.waben.option.common.model.dto.user.logger;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserLoggerDTO {

    private Long userId;

    private String cmd;

    private String cmdName;

    private String detail;

    private Map<String, Object> params;

    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;
}
