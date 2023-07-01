package cn.foxtech.common.status.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.utils.common.utils.redis.*","cn.foxtech.common.utils.redis.status"})
public class RedisProducerStatusConfig {
}
