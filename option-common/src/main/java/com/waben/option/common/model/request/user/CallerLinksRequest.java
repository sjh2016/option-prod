package com.waben.option.common.model.request.user;

import lombok.Data;

/**
 * @author: Peter
 * @date: 2021/7/8 15:32
 */
@Data
public class CallerLinksRequest {

    private Long id;
    private String type;
    private String name;
    private String link;
    private Boolean enable;
}
