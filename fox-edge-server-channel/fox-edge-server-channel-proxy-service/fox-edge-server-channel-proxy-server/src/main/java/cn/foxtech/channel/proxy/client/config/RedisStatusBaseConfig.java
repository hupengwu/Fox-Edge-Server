package cn.foxtech.channel.proxy.client.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component：使用双接收TOPIC
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.status"})
public class RedisStatusBaseConfig {
}
