package com.waben.option.thirdparty.service.email;

import com.waben.option.thirdparty.service.email.info.EmailSendInfo;

public class EmailTest {

    public static void main(String[] args) {
        String fromAddr = "support@thewinners.cc";
        String toAddr = "645001895@qq.com";
        String title = "【测试标题】Testing Subject-myself-TEXT";
//        String title = "【测试标题】Testing Subject-myself-HTML";
//        String title = "【测试标题】Testing Subject-myself-eml文件";
//        String title = "【测试标题】Testing Subject-myself-eml文件_含多个附件";
        String content = "【测试内容】Hello, this is sample for to check send email using JavaMailAPI ";
        String port = "25";
        String host = "email-smtp.us-east-1.amazonaws.com";
        String userName = "AKIA5N52HYRCPXCCGQ54";
        String password = "BIgDo8lFS58jNz33yV7l+YpNZ3yzZ+cl60fqh0omEW3X";

        EmailSendInfo mailInfo = new EmailSendInfo();
        mailInfo.setMailServerHost(host);
        mailInfo.setMailServerPort(port);
        mailInfo.setValidate(false);
        mailInfo.setUserName(userName);
        mailInfo.setPassword(password);
        mailInfo.setFromAddress(fromAddr);
        mailInfo.setToAddress(toAddr);
        mailInfo.setSubject(title);
        mailInfo.setContent(content);
//        mailInfo.setAttachFileNames(new String[]{"file/XXX.jpg", "file/XXX.txt"});

        //发送文体格式邮件
        EmailSender.sendTextMail(mailInfo);
        //发送html格式邮件
//         EmailSender.sendHtmlMail(mailInfo);
        //发送含附件的邮件
//        EmailSender.sendAttachmentMail(mailInfo);
    }
}
