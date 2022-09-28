package com.waben.option.common.model.dto.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/7/16 17:48
 */
@Data
public class SunshineDTO {

    private Long id;

    private Long userId;

    private SunshineTypeEnum type;

    private String username;

    private String url;

    private Boolean enable;

    private String localDate;

    private LocalDateTime gmtCreate;
}
