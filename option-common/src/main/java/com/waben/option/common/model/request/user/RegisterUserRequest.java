package com.waben.option.common.model.request.user;

import com.waben.option.common.model.enums.AuthorityEnum;
import com.waben.option.common.model.enums.PlatformEnum;
import com.waben.option.common.model.enums.RegisterEnum;
import lombok.Data;

@Data
public class RegisterUserRequest {

    private String username;

    private String password;

    private RegisterEnum registerType = RegisterEnum.PHONE;

    private AuthorityEnum authorityType;

    private PlatformEnum platform;

    private String verifyCode;

    private String areaCode;

    /**
     * 1:普通注册账号
     * 2：google注册
     * 3：face_book注册
     */
    private Integer source;

    private String ip;

    private String symbolCode;

    private String sessionId;

    private String verifyImageCode;

    private String topId;
}
