package com.waben.option.thirdparty.service.sms.amazon;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class Test {

    private static void sendmail(String sender, String title, String content, String email) {
        //判断发送邮件的参数是否正确
        if (sender != null && !"".equals(sender) && title != null && !"".equals(title) && email != null && !"".equals(email)) {
            String myEmailAccount = "galawxy@163.com";
            //授权码  163邮箱的授权码sccist8663    QQ邮箱的授权码cqbvzjwtktcxeacb
            String myEmailPassword = "sccist8663";
            // 发件人邮箱的 SMTP 服务器地址, 必须准确, 不同邮件服务器地址不同, 一般(只是一般, 绝非绝对)格式为: smtp.xxx.com   网易163邮箱的 SMTP 服务器地址为: smtp.163.com
            String myEmailSMTPHost = "email-smtp.us-east-1.amazonaws.com";
            // 收件人邮箱（替换为自己知道的有效邮箱）
            String receiveMailAccount = email;
            //邮件发送 附件文件名过长的问题 版本1.5及以上
            System.getProperties().setProperty("mail.mime.splitlongparameters", "false");
            // 1. 创建参数配置, 用于连接邮件服务器的参数配置
            Properties props = new Properties();                    // 参数配置s
            props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
            props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
            props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
            // PS: 某些邮箱服务器要求 SMTP 连接需要使用 SSL 安全认证 (为了提高安全性, 邮箱支持SSL连接, 也可以自己开启),
            //     如果无法连接邮件服务器, 仔细查看控制台打印的 log, 如果有有类似 “连接失败, 要求 SSL 安全连接” 等错误,
            //     打开下面 /* ... */ 之间的注释代码, 开启 SSL 安全连接。
            // SMTP 服务器的端口 (非 SSL 连接的端口一般默认为 25, 可以不添加, 如果开启了 SSL 连接,
            //                  需要改为对应邮箱的 SMTP 服务器的端口, 具体可查看对应邮箱服务的帮助,
            //                  QQ邮箱的SMTP(SLL)端口为465或587, 其他邮箱自行去查看)
            final String smtpPort = "465";
            props.setProperty("mail.smtp.port", smtpPort);
            props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", smtpPort);
            // 2. 根据配置创建会话对象, 用于和邮件服务器交互
            Session session = Session.getInstance(props);
            session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log
            // 1. 创建一封邮件
            MimeMessage message = new MimeMessage(session);
            try {
                // 2. From: 发件人（昵称有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改昵称）
                message.setFrom(new InternetAddress(myEmailAccount, sender, "UTF-8"));
                // 3. To: 收件人（可以增加多个收件人、抄送、密送）
                message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMailAccount, "用户", "UTF-8"));
                // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
                message.setSubject(title, "UTF-8");
                // 5. Content: 邮件正文（可以使用html标签）（内容有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改发送内容）
                message.setContent(content, "text/html;charset=UTF-8");

                    /*//发送附件
                    Multipart multipart = new MimeMultipart();
                    if (attachment != null) {
                        BodyPart attachmentBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(attachment);
                        attachmentBodyPart.setDataHandler(new DataHandler(source));

                        // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                        // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                        //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                        //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");

                        //MimeUtility.encodeWord可以避免文件名乱码
                        //attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));

                        //截取文件地址最后“\\”后面的地址
                        String str = attachment.toString();
                        int xiabiao = str.lastIndexOf("\\");

                        attachmentBodyPart.setFileName(MimeUtility.encodeText(str.substring(xiabiao, str.length()), "utf-8", "B"));
                        multipart.addBodyPart(attachmentBodyPart);
                    }
                    MimeBodyPart textContent = new MimeBodyPart();
                    //添加邮件正文
                    textContent.setText(content, "UTF-8");
                    //添加MimeBodyPart到Multipart中
                    multipart.addBodyPart(textContent);
                    //添加附件
                    message.setContent(multipart);*/
                // 6. 设置发件时间
                message.setSentDate(new Date());
                // 7. 保存设置
                message.saveChanges();
                // 4. 根据 Session 获取邮件传输对象
                Transport transport = session.getTransport();
                // 5. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
                //
                //    PS_01: 成败的判断关键在此一句, 如果连接服务器失败, 都会在控制台输出相应失败原因的 log,
                //           仔细查看失败原因, 有些邮箱服务器会返回错误码或查看错误类型的链接, 根据给出的错误
                //           类型到对应邮件服务器的帮助网站上查看具体失败原因。
                //
                //    PS_02: 连接失败的原因通常为以下几点, 仔细检查代码:
                //           (1) 邮箱没有开启 SMTP 服务;
                //           (2) 邮箱密码错误, 例如某些邮箱开启了独立密码;
                //           (3) 邮箱服务器要求必须要使用 SSL 安全连接;
                //           (4) 请求过于频繁或其他原因, 被邮件服务器拒绝服务;
                //           (5) 如果以上几点都确定无误, 到邮件服务器网站查找帮助。
                //
                //    PS_03: 仔细看log, 认真看log, 看懂log, 错误原因都在log已说明。
                transport.connect(myEmailAccount, myEmailPassword);
                // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
                transport.sendMessage(message, message.getAllRecipients());
                // 7. 关闭连接
                transport.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("邮件消息缺少，无法进行发送");
        }
    }
}
