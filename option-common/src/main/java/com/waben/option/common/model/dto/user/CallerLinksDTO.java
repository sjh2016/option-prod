package com.waben.option.common.model.dto.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: Peter
 * @date: 2021/7/8 13:59
 */
@Data
public class CallerLinksDTO {

    private Long id;

    private String name;

    private String link;

    private String type;

    private Boolean enable;

    private LocalDateTime gmtCreate;
}
