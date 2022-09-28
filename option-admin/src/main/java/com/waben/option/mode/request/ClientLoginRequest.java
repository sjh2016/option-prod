package com.waben.option.mode.request;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ClientLoginRequest {

    @NotBlank(message = "1030")
    @ApiModelProperty(value = "账号")
    private String username;

    @NotBlank(message = "1030")
    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "区号")
    private String areaCode;

    @NotNull(message = "1030")
    @ApiModelProperty(value = "用户类型")
    private AuthorityEnum authorityType;

    @ApiModelProperty(value = "登录终端")
    private PlatformEnum platform = PlatformEnum.H5;

}
