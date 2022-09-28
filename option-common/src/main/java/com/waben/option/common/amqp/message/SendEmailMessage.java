package com.waben.option.common.amqp.message;

import com.waben.option.common.model.enums.EmailTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailMessage {

    private String toEmail;

    private String code;

    private String content;

    private EmailTypeEnum type;
    
    /**
     * 1 亚马逊
     * 2 腾讯云
     */
    private Integer apiType;

}
