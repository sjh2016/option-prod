package com.waben.option.thirdparty.service.email;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;

/**
 * @author: Peter
 * @date: 2021/7/7 14:41
 */
public class EmailSendTest {

    public static void main(String[] args) throws MessagingException {
        EmailSendTest.sendGroupMail();
    }

    public static String[] getRecipients() {
        File recipientsFile = new File("D:/peter_work/Recipients.txt");
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(recipientsFile);
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder builder = new StringBuilder();
            // 读入联系人
            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append(",");
            }
            // 将联系人分割为数组返回
            return builder.toString().split(",");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void sendGroupMail() throws AddressException, MessagingException {
        // 得到Session
        Properties props = //getProperties();
                new Properties();
        props.setProperty("mail.host", "smtp.163.com");
        props.setProperty("mail.smtp.port", "25");
        props.setProperty("mail.smtp.auth", "true");
//        props.setProperty("mail.smtp.ssl.enable", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //邮箱的用户名和密码
                return new PasswordAuthentication("test@163.com", "QVYARMNRSWSJTCJC");
            }
        };
        Session session = Session.getInstance(props, authenticator);

        //发送邮件
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("test@163.com"));
        String[] recipients = getRecipients();
        for (String rec : recipients) {
            //设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(rec));
            //设置标题
            message.setSubject("JavaMail测试邮件!");
            //设置正文
            message.setContent("群发邮件，如有打扰请见谅!", "text/html;charset=utf-8");
            //发送
            Transport.send(message);
        }
    }


    public void setMultipart(MimeMessage msg) throws MessagingException,
            IOException {
        // 一个Multipart对象包含一个或多个BodyPart对象，来组成邮件的正文部分（包括附件）。
        MimeMultipart multiPart = new MimeMultipart();

        // 添加正文
        MimeBodyPart partText = new MimeBodyPart();
        partText.setContent("这是一封含有附件的群发邮件!", "text/html;charset=utf-8");

        // 添加文件 也就 是附件
        MimeBodyPart partFile = new MimeBodyPart();
        File file = new File("E:mail.jar");
        partFile.attachFile(file);
        // 设置在收件箱中和附件名 并进行编码以防止中文乱码
        partFile.setFileName(MimeUtility.encodeText(file.getName()));

        multiPart.addBodyPart(partText);
        multiPart.addBodyPart(partFile);
        msg.setContent(multiPart);
    }

    public void sendMultipartMail() throws AddressException, MessagingException,
            IOException {
        // 得到Session
        Properties props = new Properties();
        props.setProperty("mail.host", "smtp.163.com");
        props.setProperty("mail.smtp.auth", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // 邮箱的用户名和密码
                return new PasswordAuthentication("gyx2110", "********");
            }
        };
        Session session = Session.getInstance(props, authenticator);

        // 发送邮件
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("gyx2110@163.com"));
        // 设置收件人
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(
                "gyx2110@sina.com"));
        // 设置标题
        message.setSubject("JavaMail带附件的测试邮件!");
        // 设置邮件主体
        setMultipart(message);
        // 发送
        Transport.send(message);
    }
}
