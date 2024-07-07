package cn.foxtech.kernel.system.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = { "cn.foxtech.common.mqtt"})
public class MqttConfig {
}
