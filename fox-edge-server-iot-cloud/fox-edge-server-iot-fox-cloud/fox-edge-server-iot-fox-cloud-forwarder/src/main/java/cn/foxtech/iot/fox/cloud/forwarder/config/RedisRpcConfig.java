package cn.foxtech.iot.fox.cloud.forwarder.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component：使用双接收TOPIC
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.rpc.redis.*"})
public class RedisRpcConfig {
}

