package com.one.frontend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationMail(String to, String verificationUrl) {
        // 创建 MIME 邮件
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
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
}
