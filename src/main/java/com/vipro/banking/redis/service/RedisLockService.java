package com.vipro.banking.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisLockService {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public boolean lockAccount(Long accountId){
        String key ="lock:account:"+accountId;
         Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, "LOCKED", 5, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(locked);
    }
    public void unlockAccount(Long accountId){
        redisTemplate.delete("lock:account:"+accountId);
    }

}
