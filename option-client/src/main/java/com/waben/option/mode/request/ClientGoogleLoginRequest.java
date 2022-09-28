package com.waben.option.mode.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientGoogleLoginRequest {

    @NotBlank(message = "1030")
    private String token;

    /**
     * 1:普通注册账号
     * 2：google注册
     * 3：face_book注册
     */
    @NotBlank(message = "1030")
    private Integer source;


}
