package com.one.model;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.PasswordAuthentication;
import java.util.Properties;

public class EmailTest {
    public static void main(String[] args) {
        // SMTP 认证用户名
        final String username = "AKIATX3PH4TRQ3RL3XOC";
        // SMTP 认证密码
        final String password = "BITPVUtTMrN3TdSZytWOYtNgQlWzOnA2qJRtc8gzQ6rC";

        Properties prop = new Properties();
        // SMTP 服务器地址
        prop.put("mail.smtp.host", "email-smtp.ap-northeast-1.amazonaws.com");
        // SMTP 端口
        prop.put("mail.smtp.port", "465");
        // 需要认证
        prop.put("mail.smtp.auth", "true");
        // 启用 TLS
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.starttls.enable", "false");

        // 创建会话并传入认证
        Session session = Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            // 创建消息对象
            Message message = new MimeMessage(session);
            // 发件人
            message.setFrom(new InternetAddress("smile3541a@email.com"));
            // 收件人
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("recipient@email.com"));
            // 邮件主题
            message.setSubject("Test Email");
            // 邮件内容
            message.setText("This is a test email from JavaMail API");

            // 发送邮件
            Transport.send(message);

            System.out.println("Email sent successfully");

        } catch (MessagingException e) {
            e.printStackTrace(); // 打印异常信息
        }
    }
}
