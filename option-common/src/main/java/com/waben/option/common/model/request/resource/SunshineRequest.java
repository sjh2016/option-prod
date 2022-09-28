package com.waben.option.common.model.request.resource;

import com.waben.option.common.model.enums.SunshineTypeEnum;
import lombok.Data;

/**
 * @author: Peter
 * @date: 2021/7/16 17:56
 */
@Data
public class SunshineRequest {

    private Long id;

    private Long userId;

    private String url;

    private Boolean enable;

    private SunshineTypeEnum type;

}
