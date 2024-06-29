package cn.foxtech.kernel.system.common.redistopic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 发布者
 */
@Component
public class RedisTopicPublisher {
    @Autowired
    protected RedisTemplate redisTemplate;

    public void sendMessage(String topic, Object message) {
        this.redisTemplate.convertAndSend(topic, message);
    }
}
