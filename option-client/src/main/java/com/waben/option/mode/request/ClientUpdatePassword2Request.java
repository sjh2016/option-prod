package com.waben.option.mode.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientUpdatePassword2Request {

	private Long userId;
	
	private String username;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "1030")
    private String verifyCode;

    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "1030")
    private String newPassword;

    @ApiModelProperty(value = "区号")
    private String areaCode;

}
