/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {//
        "cn.foxtech.kernel.system.auth",//
        "cn.foxtech.persist.mysql",//
        "cn.foxtech.controller.service",//
        "cn.foxtech.period.service"//
})
public class SystemComposeConfig {
}
