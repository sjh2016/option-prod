package com.waben.option.thirdparty.service.sms.aliyun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DaiHunEmailService extends AbstractSingleEmailService {

    @Value("${email.ali.accessKey:LTAI5tLkLbaYFrCKD4w73owy}")
    private String accessKey;

    @Value("${email.ali.secret:l1ACFjBVbEBFVzR7WNuugRXERCuWhp}")
    private String secretKey;

    @Value("${email.ali.subject:Verifikasi pendaftaran My SolarPower}")
    private String subject;

    @Value("${email.ali.tigName:My Solarpower}")
    private String tagName;

    @Value("${email.ali.accountName:service@reg.mysolarpowerregister.com}")
    private String accountName;

    @Value("${email.ali.fromAlias:My SolarPower}")
    private String fromAlias;

    @Override
    protected String getAccessKey() {
        return this.accessKey;
    }

    @Override
    protected String getAccountName() {
        return this.accountName;
    }

    @Override
    protected String getSubject() {
        return subject;
    }

    @Override
    protected String getTagName() {
        return tagName;
    }

    @Override
    protected String getSecretKey() {
        return secretKey;
    }

    @Override
    protected String getFromAlias() {
        return fromAlias;
    }
}
