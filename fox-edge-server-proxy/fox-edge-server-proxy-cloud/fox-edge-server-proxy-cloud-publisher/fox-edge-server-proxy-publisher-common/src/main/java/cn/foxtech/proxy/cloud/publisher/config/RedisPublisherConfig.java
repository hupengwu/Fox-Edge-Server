package cn.foxtech.proxy.cloud.publisher.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 实例化RedisService组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.proxy.cloud.publisher.service"})
public class RedisPublisherConfig {
}
