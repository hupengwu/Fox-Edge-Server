package cn.foxtech.value.ex.task.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 * trigger没有使用数据库，所以不能MapperScan
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.entity.manager"})
public class EntityConfig {
}


