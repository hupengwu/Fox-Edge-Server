/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {"cn.foxtech.utils.common.utils.redis.*"})
public class RedisConfig {
}

