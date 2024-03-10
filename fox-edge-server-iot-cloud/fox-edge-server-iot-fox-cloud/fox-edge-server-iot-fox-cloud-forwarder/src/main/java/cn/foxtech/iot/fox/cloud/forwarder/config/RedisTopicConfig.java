package cn.foxtech.iot.fox.cloud.forwarder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.utils.redis.topic"})
public class RedisTopicConfig {
}

