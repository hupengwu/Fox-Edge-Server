/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component：使用双接收TOPIC
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.common.tags"})
public class RedisTagConfig {
}
