package com.one.frontend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${custom.mail.from}")
    private String from;

    public void sendVerificationMail(String to, String verificationUrl) {
        // 创建 MIME 邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("升級再來一抽認證會員");
            // 将邮件内容设置为 HTML 格式
            String htmlContent = "<p>請點擊網址升級成認證會員感謝您:</p>" +
                    "<a href=\"" + verificationUrl + "\">" + verificationUrl + "</a>";
            helper.setText(htmlContent, true); // 第二个参数设为 true 表示内容为 HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // 处理邮件发送异常
        }
    }

    public void sendRecImg(String username, ResponseEntity<byte[]> response) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // 第二个参数为 true 以支持附件
        byte[] receiptRes = response.getBody();

        try {
            helper.setFrom(from);
            helper.setTo(username);
            helper.setSubject("感謝您在再來一抽消費");

            // 设置邮件内容
            String htmlContent = "<p>感謝您在再來一抽消費，請查看附件中的發票圖片。</p>";
            helper.setText(htmlContent, true); // 第二个参数设为 true 表示内容为 HTML

            // 添加附件，文件名可以自定义
            helper.addAttachment("receipt.png", new ByteArrayDataSource(receiptRes, "image/png"));

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // 处理邮件发送异常
        }
    }


    public void sendPrizeMail(String to) {
        // 创建 MIME 邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("賞品盒數量達標通知");
            // 将邮件内容设置为 HTML 格式
            String htmlContent = "<p>賞品盒數量已達100個，請盡快整理賞品盒，如達到150個後則不能進行抽獎</p>";
            helper.setText(htmlContent, true); // 第二个参数设为 true 表示内容为 HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // 处理邮件发送异常
        }
    }

    public void sendPassWordMail(String to, String verificationUrl) {
        // 创建 MIME 邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("忘記密碼連結");
            // 将邮件内容设置为 HTML 格式
            String htmlContent = "<p>請點擊網址重設密碼:</p>" +
                    "<a href=\"" + verificationUrl + "\">" + verificationUrl + "</a>";
            helper.setText(htmlContent, true); // 第二个参数设为 true 表示内容为 HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace(); // 处理邮件发送异常
        }
    }
}
