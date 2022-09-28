/*
package com.waben.option.thirdparty.service.sms.amazon;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

import java.util.HashMap;
import java.util.Map;

public class ShortMessage {

    public PublishResult sendSMSMessage(String phoneNumber, String message) {
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        smsAttributes.put("AWS.SNS.SMS.SenderID", new MessageAttributeValue()
                .withStringValue("mySenderID") //The sender ID shown on the device.
                .withDataType("String"));
        smsAttributes.put("AWS.SNS.SMS.MaxPrice", new MessageAttributeValue()
                .withStringValue("0.50") //Sets the max price to 0.50 USD.
                .withDataType("Number"));
        smsAttributes.put("AWS.SNS.SMS.SMSType", new MessageAttributeValue()
                .withStringValue("Promotional") //Sets the type to promotional.
                .withDataType("String"));
        return sendSMSMessage(phoneNumber, message, smsAttributes);
    }

    public PublishResult sendSMSMessage(String phoneNumber, String message, Map<String, MessageAttributeValue> smsAttributes) {
        AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIA5N52HYRCGHOQHNNQ"; // 带有发短信权限的 IAM 的 ACCESS_KEY
            }

            @Override
            public String getAWSSecretKey() {
                return "cEwNgn/GKOVPAvoLPbbkH3eQy3MaeB4zvDVOa2Xm"; // 带有发短信权限的 IAM 的 SECRET_KEY
            }
        };
        AWSCredentialsProvider provider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return awsCredentials;
            }

            @Override
            public void refresh() {
            }
        };
        AmazonSNS amazonSNS = null;
        try {
            amazonSNS = AmazonSNSClientBuilder.standard().withCredentials(provider).withRegion("us-east-1").build();
        } catch (Exception e) {

        }
        return amazonSNS.publish(
                new PublishRequest()
                        .withMessage(message)
                        .withPhoneNumber(phoneNumber)
                        .withMessageAttributes(smsAttributes)
        );
    }

    public static void main(String[] args) {
        //AmazonSNSClient client = new AmazonSNSClient(credentialsProvider);
        //AWSCredentialsProviderChain chain = new AWSCredentialsProviderChain(credentialsProvider);
        //chain.setReuseLastProvider(true);
        //credentialsProvider.getCredentials();
        ShortMessage shortMessage = new ShortMessage();
        PublishResult publishResult = shortMessage.sendSMSMessage("+6309054960416", "888888");
        System.out.println(publishResult);
    }

}
*/
