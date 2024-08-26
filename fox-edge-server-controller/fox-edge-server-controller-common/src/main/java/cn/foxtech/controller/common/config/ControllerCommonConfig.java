/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.controller.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {//
        "cn.foxtech.common.entity.manager",//
        "cn.foxtech.utils.common.utils.redis.*",//
        "cn.foxtech.common.rpc.redis.*",//
        "cn.foxtech.common.rpc.cache",//
        "cn.foxtech.common.status",//
})
public class ControllerCommonConfig {
}

