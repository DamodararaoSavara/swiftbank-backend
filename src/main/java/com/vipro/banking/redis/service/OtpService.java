package com.vipro.banking.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    @Autowired
    private RedisTemplate<String,String > redisTemplate;

    public String generateOtp(Long accountId) {
      String otp =  String.valueOf(100000+new SecureRandom().nextInt(900000));
        redisTemplate.opsForValue().set("otp:transfer:"+accountId,otp,90, TimeUnit.SECONDS);
        return otp;
    }

    public void validateOtp(Long accountId, String inputOtp) {
        String key = "otp:transfer:" + accountId;
         String storedOtp = redisTemplate.opsForValue().get(key);

         if(storedOtp==null){
             throw new RuntimeException("OTP expired or not found");
         }
        if (!storedOtp.equals(inputOtp)) {
            throw new RuntimeException("Invalid OTP");
        }
        // 🔥 OTP is one-time use
        redisTemplate.delete(key);
    }

}
