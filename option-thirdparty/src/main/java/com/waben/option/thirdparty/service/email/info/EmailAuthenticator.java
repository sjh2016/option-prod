package com.waben.option.thirdparty.service.email.info;

import lombok.Data;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


@Data
public class EmailAuthenticator extends Authenticator {

    private String userName;
    private String password;

    public EmailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
