/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.config;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 */
@Configuration
@ComponentScan(basePackages = {//
        "cn.foxtech.common.entity.service", //
        "cn.foxtech.common.entity.manager", //
        "cn.foxtech.common.mqtt",//
        "cn.foxtech.kernel.common.*",//
        "cn.foxtech.common.status",//
        "cn.foxtech.common.file",//
        "cn.foxtech.kernel.system.common",//
        "cn.foxtech.kernel.system.repository"//
})
@MapperScan("cn.foxtech.common.entity.service")
public class SystemEngineConfig {
}

