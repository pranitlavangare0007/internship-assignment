package com.pranit.assignment.Scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class NotificationScheduler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(fixedRate = 300000) // 5 minutes
    public void processNotifications() {

        Set<String> keys = redisTemplate.keys("user:*:pending_notifs");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {

            List<String> messages = redisTemplate.opsForList().range(key, 0, -1);

            if (messages == null || messages.isEmpty()) continue;

            int count = messages.size();

            String userId = key.split(":")[1];

            System.out.println(
                    "Summarized Push Notification: Bot X and " + count +
                            " others interacted with your posts (User " + userId + ")"
            );

            redisTemplate.delete(key);
        }
    }
}
