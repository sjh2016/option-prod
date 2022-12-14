package com.waben.option.thirdparty.service.email.info;

import lombok.Data;

import java.util.Properties;

@Data
public class EmailSendInfo {

    /**
     * 发送邮件的服务器的IP
     */
    private String mailServerHost;
    /**
     * 端口
     */
    private String mailServerPort = "25";
    /**
     * 邮件发送者的地址
     */
    private String fromAddress;
    /**
     * 邮件接收者的地址
     */
    private String toAddress;
    /**
     * 登陆邮件发送服务器的用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 是否需要身份验证
     */
    private boolean validate = false;
    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件的文本内容
     */
    private String content;
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;

    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }
}
