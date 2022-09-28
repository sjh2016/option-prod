package com.waben.option.thirdparty.amqp;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailTest {

	public static void testMain(String[] args) throws Exception {
		
		Session session = Session.getDefaultInstance(getProperties());
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress("support@schroders.info", "support"));
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress("mengqilufei147@gmail.com"));
        msg.setSubject("测试111");
        msg.setContent("<p>你好111</p>", "text/html;charset=utf-8");
        Transport transport = session.getTransport();
        System.out.println("1111111111111111");
        transport.connect("194.163.40.62", "support", "portsup@123");
        System.out.println("2222222222222222");
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
	}
	
	private static Properties getProperties() {
        Properties props = System.getProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        return props;
    }
	
}
