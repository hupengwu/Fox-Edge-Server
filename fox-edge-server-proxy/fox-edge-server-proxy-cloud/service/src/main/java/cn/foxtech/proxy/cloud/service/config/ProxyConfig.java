package cn.foxtech.proxy.cloud.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 实例化RedisTemplate组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.proxy.cloud.common.*","cn.foxtech.proxy.cloud.forwarder.*","cn.foxtech.proxy.cloud.publisher.*"})
public class ProxyConfig {
}
