package com.waben.option.common.model.request.user;

import lombok.Data;

@Data
public class UpdatePassword1Request {

    private Long userId;

    private String oldPassword;

    private String newPassword;

}
