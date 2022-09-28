package com.waben.option.thirdparty.service.sms.amazon;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.waben.option.common.constants.RedisKey;
import com.waben.option.common.exception.ServerException;
import com.waben.option.thirdparty.service.sms.AbstractBaseSmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AmazonSmsService extends AbstractBaseSmsService {

    @Value("${sms.amazon.accessKey:AKIAZM34MSAII2RQJIMU}")
    private String accessKey;

    @Value("${sms.amazon.secret:59rlwVWQgEDqOawcNnkoX/CjeHKJ+YBuJzbWl/fP}")
    private String secretKey;

    @Value("${sms.amazon.body:Dear customers, Your verification code is: %s}")
    private String smsBody;

    @Value("${sms.amazon.expiredSeconds:300}")
    private int expiredSeconds;

    @Resource
    protected RedisTemplate<Serializable, Object> redisTemplate;

    @Override
    public boolean sendCode(String areaCode, String phone, String code, String content, String ip) {
        /*AmazonSNSClientBuilder clientBuilder = AmazonSNSClientBuilder.standard();
        // 如何获取accessKey?  https://console.aws.amazon.com/iam/home
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AWSStaticCredentialsProvider provider = new AWSStaticCredentialsProvider(awsCredentials);
        clientBuilder.setCredentials(provider);
        // Regions 就是你选择从哪个国家的服务器接入,价格参考 https://amazonaws-china.com/cn/sns/sms-pricing/
        clientBuilder.setRegion(Regions.AP_SOUTHEAST_1.getName());
        //  AmazonSNSClient amazonSNSClient = (AmazonSNSClient) clientBuilder.build();
        String mobilePhone = areaCode + phone;
        String msg = String.format(smsBody, code);
        return sendSMSMessage(clientBuilder, msg, mobilePhone, code);*/
        verifyRequestCount(ip);
        String msg = String.format(smsBody, code);
        if (content != null) msg = content;
        BasicAWSCredentials basicAwsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonSNS snsClient = AmazonSNSClient
                .builder()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(basicAwsCredentials))
                .build();
        return sendSMSMessage(snsClient, msg, areaCode, phone, code);
    }

    private boolean sendSMSMessage(AmazonSNS snsClient,
                                   String message, String areaCode, String mobilePhone, String code) {
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(areaCode + mobilePhone)
                .withMessageAttributes(getMessageMap()));
        log.info("sendSMSMessage:{}|{}|{}|{}|{}|{}", result.toString(), areaCode, mobilePhone, code, expiredSeconds, message);
        if (result.getMessageId() != null) {
            String key = RedisKey.getKey(RedisKey.OPTION_SYSTEM_VERIFY_CODE, mobilePhone);
            redisTemplate.opsForValue().set(key, code);
            redisTemplate.expire(key, expiredSeconds, TimeUnit.SECONDS);
            return true;
        }
        throw new ServerException(1032);
    }

    private Map<String, MessageAttributeValue> getMessageMap() {
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("CNY") //The sender ID shown on the device.
                .withDataType("String"));
//        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
//                .withStringValue("0.50") //Sets the max price to 0.50 USD.
//                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Transactional") //Sets the type to Promotional/Transactional
                .withDataType("String"));

        return smsAttributes;
    }
}
