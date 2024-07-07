package cn.foxtech.iot.fox.cloud.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 实例化RedisTemplate组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.iot.fox.cloud.common.*","cn.foxtech.iot.fox.cloud.forwarder.*","cn.foxtech.iot.fox.cloud.publisher.*"})
public class ProxyConfig {
}
