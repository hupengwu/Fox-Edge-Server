package cn.foxtech.proxy.redis.topic.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {
        "cn.foxtech.common.utils.redis.topic",
        "cn.foxtech.common.status"
})
public class RedisTopicConfig {
}

