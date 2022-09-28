package com.waben.option.mode.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClientUpdateUserFreezeRequest {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "是否冻结")
    private Boolean freeze;

    @ApiModelProperty(value = "正常/拉黑")
    private Boolean enable;

}
