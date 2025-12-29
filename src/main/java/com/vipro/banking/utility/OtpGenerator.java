package com.vipro.banking.utility;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
    public String generateOtp() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
