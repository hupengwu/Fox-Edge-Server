/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.controller.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描controller.common的组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.controller.common.*"})
public class ControllerConfig {
}
