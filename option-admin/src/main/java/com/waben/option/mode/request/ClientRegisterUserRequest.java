package com.waben.option.mode.request;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientRegisterUserRequest {

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "1030")
    private String username;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "1030")
    private String password;

    @ApiModelProperty(value = "注册类型")
    private RegisterEnum registerType;

    @ApiModelProperty(value = "用户类型")
    private AuthorityEnum authorityType;

    @ApiModelProperty(value = "注册终端")
    private PlatformEnum platform;

    @ApiModelProperty(value = "验证码", hidden = true)
    private String verifyCode;

    @ApiModelProperty(value = "区号")
    private String areaCode;

    @ApiModelProperty(value = "注册IP")
    private String ip;

    /**
     * 1:普通注册账号
     * 2：google注册
     * 3：face_book注册
     */
    @ApiModelProperty(value = "账号来源")
    private Integer source;

    @ApiModelProperty(value = "层级邀请码")
    private String symbolCode;

    private String sessionId;

    private String verifyImageCode;

    @ApiModelProperty(value = "topId")
    private String topId;
}
