package com.pranit.assignment.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/test-redis")
    public String testRedis() {
        redisTemplate.opsForValue().set("test:key", "Hello Redis Cloud");
        return redisTemplate.opsForValue().get("test:key");
    }
}
