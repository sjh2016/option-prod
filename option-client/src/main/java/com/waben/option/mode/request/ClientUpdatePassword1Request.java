package com.waben.option.mode.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientUpdatePassword1Request {

	private Long userId;

    @ApiModelProperty(value = "旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "1030")
    private String newPassword;

}
