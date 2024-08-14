/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.iotdb.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描controller.common的组件
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.persist.common.*"})
public class PersistServiceConfig {
}
