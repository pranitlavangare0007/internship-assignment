package com.pranit.assignment.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ViralityService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void updateScore(Long postId, String type) {

        int points = switch (type) {
            case "BOT_REPLY" -> 1;
            case "HUMAN_LIKE" -> 20;
            case "HUMAN_COMMENT" -> 50;
            default -> 0;
        };

        String key = "post:" + postId + ":virality_score";


        redisTemplate.opsForValue().increment(key, points);
    }
}
