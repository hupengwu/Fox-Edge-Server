/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.service.config;

//import org.mybatis.spring.annotation.MapperScan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 告知Spring框架去扫描其他包中的Component
 * Device服务不使用数据库，所以不通过扫描map来实例化MyBatis组件
 */
@Configuration
@ComponentScan(basePackages = { "cn.foxtech.common.entity.manager"})
public class EntityConfig {
}

