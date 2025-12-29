package com.vipro.banking.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String to, String otp){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("admin@gmail.com");
        message.setTo(to);
        message.setSubject("Verify Your Bank Account");
        message.setText(
                "Dear Customer,\n\n" +
                        "We received a request to verify your identity.\n\n" +
                        "Your OTP: " + otp + "\n\n" +
                        "• Valid for 90 seconds\n" +
                        "• Do not share this code with anyone\n\n" +
                        "If this request was not initiated by you, please contact our support team immediately.\n\n" +
                        "Thank you for choosing Vipro Banking.\n\n" +
                        "— Vipro Banking Security Team"
        );
        mailSender.send(message);
    }
}
