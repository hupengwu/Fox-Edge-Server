package cn.foxtech.controller.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.entity.manager"})
public class EntityConfig {
}

