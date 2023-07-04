package cn.foxtech.trigger.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 * trigger没有使用数据库，所以不能MapperScan
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.entity.service","cn.foxtech.common.entity.manager"})
@MapperScan("cn.foxtech.common.entity.service")
public class EntityConfig {
}


