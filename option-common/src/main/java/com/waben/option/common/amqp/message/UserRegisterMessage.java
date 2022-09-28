package com.waben.option.common.amqp.message;

import lombok.Data;

@Data
public class UserRegisterMessage {

    private Long userId;

    private Long parentId;

    public UserRegisterMessage() {

    }

    public UserRegisterMessage(Long userId) {
        this.userId = userId;
    }

    public UserRegisterMessage(Long userId, Long parentId) {
        this.userId = userId;
        this.parentId = parentId;
    }

}
