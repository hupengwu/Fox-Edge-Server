package cn.foxtech.channel.hikvision.fire.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.channel.common","cn.foxtech.channel.socket.core"})
public class CommonConfig {
}



