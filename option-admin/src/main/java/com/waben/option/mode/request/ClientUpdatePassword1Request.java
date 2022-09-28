package com.waben.option.mode.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientUpdatePassword1Request {

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "1030")
    private String username;

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "1030")
    private String newPassword;

}
