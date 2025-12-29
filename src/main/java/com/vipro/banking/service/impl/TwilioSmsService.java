/*
package com.vipro.banking.service.impl;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.vipro.banking.service.SmsService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(accountSid, authToken);
    }

    @Override
    public void sendOtp(String phone, String otp) {
        if (!phone.startsWith("+")) {
            phone = "+91" + phone;
        }
        System.out.println("fromNumber==>"+fromNumber);
        System.out.println("phone==>"+phone);
        Message.creator(
                new com.twilio.type.PhoneNumber(phone),
                new com.twilio.type.PhoneNumber(fromNumber),
                "SwiftBank Verification Code\n" +
                        "OTP: " + otp + "\n" +
                        "Expires in 5 minutes.\n" +
                        "If you didn't request this, ignore this message."
        ).create();
    }
}
*/
