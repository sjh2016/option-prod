package com.waben.option.common.model.dto.resource;

import lombok.Data;

/**
 * @author: Peter
 * @date: 2021/6/2 15:47
 */
@Data
public class LanguageDTO {

    private Long id;

    private String code;

    private String locale;

    private String name;

    private String englishName;

    private String countryCode;

    private Boolean enable;

    private Integer sort;
}
