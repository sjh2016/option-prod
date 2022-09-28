package com.waben.option.common.model.request.user;

import lombok.Data;

@Data
public class UpdateUserFreezeRequest {

    private Long userId;

    private Boolean freeze;

    private Boolean enable;

}
