/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.demo.service.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描cn.foxtech.service.common的组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.service.common.*"})
@MapperScan("cn.foxtech.demo.service")
public class DemoEngineConfig {
}
