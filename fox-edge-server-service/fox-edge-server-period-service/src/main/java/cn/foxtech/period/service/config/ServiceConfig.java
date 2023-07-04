package cn.foxtech.period.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描cn.foxtech.service.common的组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.service.common.*"})
@MapperScan("cn.foxtech.period.service")
public class ServiceConfig {
}
