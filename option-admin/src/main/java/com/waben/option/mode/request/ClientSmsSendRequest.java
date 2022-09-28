package com.waben.option.mode.request;

import com.waben.option.common.model.enums.EmailTypeEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClientSmsSendRequest {

    @ApiModelProperty(value = "账号")
    private String username;

    @ApiModelProperty(value = "邮件类型")
    private EmailTypeEnum type;
    
    @ApiModelProperty(value = "手机区号")
    private String areaCode;
   
}
