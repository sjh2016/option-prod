package com.waben.option.thirdparty.service.email;

import com.waben.option.thirdparty.service.email.info.EmailAuthenticator;
import com.waben.option.thirdparty.service.email.info.EmailSendInfo;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

public class EmailSender {

    /**
     * 以文本格式发送邮件
     *
     * @param mailInfo
     * @return
     */
    public static void sendTextMail(EmailSendInfo mailInfo) {
        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new EmailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        //【调试时使用】开启Session的debug模式
        sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            MimeMessage mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject(), "UTF-8");
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent, "UTF-8");
            mailMessage.saveChanges();

            //生成邮件文件
//            createEmailFile("file/EML_myself-TEXT.eml", mailMessage);

            // 发送邮件
            Transport.send(mailMessage);
        } catch (MessagingException ex) {
        }
    }

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo
     * @return
     */
    public static boolean sendHtmlMail(EmailSendInfo mailInfo) {
        boolean sendStatus = false;//发送状态
        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        //如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new EmailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        //【调试时使用】开启Session的debug模式
        sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件内容
            mailMessage.setContent(mailInfo.getContent(), "text/html;charset=utf-8");

            //生成邮件文件
            createEmailFile("file/EML_myself-HTML.eml", mailMessage);

            // 发送邮件
            Transport.send(mailMessage);
            sendStatus = true;
        } catch (MessagingException ex) {
        }
        return sendStatus;
    }

    /**
     * 以含附件形式发送邮件
     *
     * @param mailInfo
     * @return
     */
    public static void sendAttachmentMail(EmailSendInfo mailInfo) {
        boolean sendStatus = false;//发送状态
        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new EmailAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        //【调试时使用】开启Session的debug模式
        sendMailSession.setDebug(true);
        try {
            // 根据session创建一个邮件消息
            MimeMessage mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject(), "UTF-8");
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart bodyPart = new MimeBodyPart();
            //设置TEXT内容
//            bodyPart.setText(mailInfo.getContent());
            // 设置HTML内容
            bodyPart.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(bodyPart);
            //设置附件
            String[] filenames = mailInfo.getAttachFileNames();
            for (int i = 0; i < filenames.length; i++) {
                // Part two is attachment
                bodyPart = new MimeBodyPart();
                String filename = filenames[i];//1.txt/sohu_mail.jpg
//                 DataSource source = new FileDataSource(filename);
                FileDataSource source = new FileDataSource(filename);
                bodyPart.setDataHandler(new DataHandler(source));
                bodyPart.setFileName(source.getName());
                mainPart.addBodyPart(bodyPart);
            }
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);

            //生成邮件文件
            createEmailFile("file/EML_reademl-eml文件_含多个附件.eml", mailMessage);

            // 发送邮件
            Transport.send(mailMessage);
        } catch (MessagingException ex) {
        }
    }

    public static void createEmailFile(String fileName, Message mailMessage)
            throws MessagingException {

        File f = new File(fileName);
        try {
            mailMessage.writeTo(new FileOutputStream(f));
        } catch (IOException e) {
        }
    }
}
