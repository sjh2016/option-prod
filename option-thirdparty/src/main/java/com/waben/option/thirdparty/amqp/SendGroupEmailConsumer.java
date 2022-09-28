package com.waben.option.thirdparty.amqp;

import com.waben.option.common.amqp.BaseAMPQConsumer;
import com.waben.option.common.amqp.message.SendEmailMessage;
import com.waben.option.common.constants.RabbitMessageQueue;
import com.waben.option.common.exception.ServerException;
import com.waben.option.common.model.enums.EmailTypeEnum;
import com.waben.option.common.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RabbitListener(containerFactory = "rabbitListenerContainerFactory", admin = "amqpAdmin", queues = RabbitMessageQueue.QUEUE_SEND_GROUP_EMAIL)
public class SendGroupEmailConsumer extends BaseAMPQConsumer<SendEmailMessage> {

    @Value("${email.customize.amazon.from:admin@service.mysolarpower.cc}")
    private String from;

    @Value("${email.customize.amazon.formName:My SolarPower}")
    private String fromName;

    @Value("${email.customize.amazon.smtpUsername:AKIAZM34MSAIPPI5K56P}")
    private String smtpUsername;

    @Value("${email.customize.amazon.smtpPassword:BId5FBGGJ2yi0svgxDDMqYeRulDyQxHEqmlkGrgGHVAN}")
    private String smtpPassword;

    @Value("${email.customize.amazon.host:email-smtp.ap-southeast-1.amazonaws.com}")
    private String host;

    @Value("${email.customize.amazon.port:587}")
    private String port;

    @Value("${email.customize.amazon.subject:Satu-satunya alamat acara offline My Solar power, THE RITZ-CARLTON, mengundang Anda untuk menginap gratis}")
    private String subject;

    private static final String ENGLISH_CONTENT_HTML_BODY = "email/english_customize_content.html";

    private volatile long lastCountTime = System.currentTimeMillis();

    private static final Map<String, Integer> requestCountMap = new ConcurrentHashMap<>();

    @Value("${max.request.limit.count:12}")
    private int maxRequestLimitCount;

    @Value("${max.request.limit.millis:1000}")
    private Long maxRequestLimitMillis;

    private synchronized void resetRequestCount(String ip) {
        Long time = System.currentTimeMillis() - lastCountTime;
        if (time.compareTo(maxRequestLimitMillis) > 0) {
            requestCountMap.clear();
            lastCountTime = System.currentTimeMillis();
            verifyRequestCount(ip);
        }
    }

    public void verifyRequestCount(String ip) {
        Long time = System.currentTimeMillis() - lastCountTime;
        if (time.compareTo(maxRequestLimitMillis) <= 0) {
            int count = requestCountMap.getOrDefault(ip, 0);
            if (count >= maxRequestLimitCount) {
                throw new ServerException(1058);
            }
            requestCountMap.put(ip, count + 1);
        } else {
            resetRequestCount(ip);
        }
    }

    @Override
    public void handle(SendEmailMessage message) {
        try {
            verifyRequestCount(message.getType().name());
            Session session = Session.getDefaultInstance(getProperties());
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, fromName));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(message.getToEmail()));
            msg.setSubject(subject);
            msg.setContent(editContext(message.getType(), message.getContent()), "text/html;charset=utf-8");
            Transport transport = session.getTransport();
            transport.connect(host, smtpUsername, smtpPassword);
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (Exception ex) {
            log.error("The email was not sent.");
            log.error("Error message: " + ex.getMessage());
        }
    }

    public InternetAddress[] getAddressArray(String address) throws AddressException {
        String[] receivers = removeSpecialChar(address).split(";");
        int len = receivers.length;
        InternetAddress[] internetAddressTo = new InternetAddress[len];
        for (int i = 0; i < len; i++) {
            if (StringUtils.isNotEmpty(receivers[i])) {
                internetAddressTo[i] = new InternetAddress(receivers[i]);
            }
        }
        return internetAddressTo;
    }

    public static String removeSpecialChar(String str) {
        String s = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            s = m.replaceAll("");
        }
        return s;
    }

    private Properties getProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }

    private String editContext(EmailTypeEnum type, String content) {
        String message = "";
        switch (type) {
            case SPECIFIC_CONTENT:
                message = HtmlUtil.readInputStream(ENGLISH_CONTENT_HTML_BODY);
                break;
            default:
                message += "Welcome to MySolarPower!";
                break;
        }
        return message;
    }
}
