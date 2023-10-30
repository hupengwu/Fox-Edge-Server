package cn.foxtech.proxy.cloud.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 实例化RedisTemplate组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.utils.common.utils.redis.*"})
public class RedisConfig {
}
