package com.waben.option.common.model.dto.user.logger;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class LoggerCommandDTO {

    private String cmd;

    private String name;

    private String detail;

    private String platform;
}
