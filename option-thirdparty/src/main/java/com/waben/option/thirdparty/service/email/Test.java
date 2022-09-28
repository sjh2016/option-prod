package com.waben.option.thirdparty.service.email;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Test {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "support@thewinners.cc");// smtp服务器地址
//        Properties props = getProperties();
        Session session = Session.getInstance(props);
        session.setDebug(true);

        Message msg = new MimeMessage(session);
        msg.setSubject("这是一个测试程序....");
        msg.setText("你好!这是我的第一个javamail程序---WQ");
        msg.setFrom(new InternetAddress("support@thewinners.cc"));//发件人邮箱(我的163邮箱)
        msg.setRecipient(Message.RecipientType.TO,
                new InternetAddress("bigwinnerkk@gmail.com")); //收件人邮箱(我的QQ邮箱)
        msg.saveChanges();

        Transport transport = session.getTransport();
        transport.connect("support@thewinners.cc", "BIgDo8lFS58jNz33yV7l+YpNZ3yzZ+cl60fqh0omEW3X");//发件人邮箱,授权码(可以在邮箱设置中获取到授权码的信息)

        transport.sendMessage(msg, msg.getAllRecipients());

        System.out.println("邮件发送成功...");
        transport.close();
    }

    private static Properties getProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }
}
