package cn.foxtech.persist.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描controller.common的组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.persist.common.*"})
public class ControllerConfig {
}
