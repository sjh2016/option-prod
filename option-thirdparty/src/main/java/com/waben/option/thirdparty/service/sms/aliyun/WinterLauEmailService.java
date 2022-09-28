package com.waben.option.thirdparty.service.sms.aliyun;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WinterLauEmailService extends AbstractSingleEmailService {

    @Value("${email.ali.accessKey:LTAI5t5zog9TB8SxaGD112Eq}")
    private String accessKey;

    @Value("${email.ali.secret:LMoj50fCAC9nsNe0Jl1j2tDfBVmlTw}")
    private String secretKey;

    @Value("${email.ali.subject:Verifikasi pendaftaran My SolarPower}")
    private String subject;

    @Value("${email.ali.tigName:My Solarpower}")
    private String tagName;

    @Value("${email.ali.accountName:service@reg.mysolarpower.cc}")
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
