package com.waben.option.common.model.request.user;

import lombok.Data;

@Data
public class UpdatePassword2Request {

    private Long userId;

    private String verifyCode;

    private String newPassword;

    private String areaCode;

}
