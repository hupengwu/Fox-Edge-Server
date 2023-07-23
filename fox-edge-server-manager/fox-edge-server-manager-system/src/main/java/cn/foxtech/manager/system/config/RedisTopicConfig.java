package cn.foxtech.manager.system.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component：使用双接收TOPIC
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.utils.redis.topic"})
public class RedisTopicConfig {
}

