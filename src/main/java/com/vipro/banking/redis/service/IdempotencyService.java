package com.vipro.banking.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class IdempotencyService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void  checkDuplicate(String requestId){
        String key = "tx:idempotency:" + requestId;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            throw new RuntimeException("Duplicate transfer request");
        }
        redisTemplate.opsForValue().set(key,"IN_PROGRESS",10, TimeUnit.SECONDS);
    }
    public void markSuccess(String requestId){
        redisTemplate.opsForValue()
                .set("tx:idempotency:" + requestId,"SUCCESS",10,TimeUnit.SECONDS);
    }
}
