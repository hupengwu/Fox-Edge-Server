package cn.foxtech.common.utils.redis.topic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 发布者
 */
@Component
public class RedisTopicPublisher {
    @Autowired
    protected StringRedisTemplate stringRedisTemplate;

    public void sendMessage(String topic, Object message) {
        this.stringRedisTemplate.convertAndSend(topic, message);
    }
}
