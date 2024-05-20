package cn.foxtech.value.ex.task.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.utils.common.utils.redis.config","cn.foxtech.utils.common.utils.redis.service"})
public class RedisConfig {
}

