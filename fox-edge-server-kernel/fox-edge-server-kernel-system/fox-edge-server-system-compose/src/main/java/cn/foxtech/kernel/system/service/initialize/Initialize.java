/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.initialize;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.controller.common.service.ControllerEnvService;
import cn.foxtech.controller.service.initialize.ControllerEngineInitialize;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.period.service.initialize.PeriodEngineInitialize;
import cn.foxtech.persist.common.service.PersistEnvService;
import cn.foxtech.persist.mysql.initialize.PersistEngineInitialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Initialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;


    @Autowired
    private SystemEngineInitialize systemEngineInitialize;

    @Autowired
    private PersistEngineInitialize persistEngineInitialize;

    @Autowired
    private ControllerEngineInitialize controllerEngineInitialize;

    @Autowired
    private PeriodEngineInitialize periodEngineInitialize;

    /**
     * 管理服务的EntityManageService
     */
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private InitialConfigService configService;

    @Autowired
    private PersistEnvService persistEnvService;

    @Autowired
    private ControllerEnvService controllerEnvService;


    public void run(String... args) {
        String message = "------------------------Manager Compose Initialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);


        // 初始化配置服务模块：强制指明使用system的EntityManageService作为主进程的EntityManageService，而不是随机指定三个中的一个
        this.configService.bindEntityManageService(this.entityManageService);
        this.systemEngineInitialize.initialize();

        // 初始化持久化服务模块
        this.persistEnvService.setCompose(true);
        this.persistEngineInitialize.initialize();

        // 初始化控制器服务模块
        this.controllerEnvService.setCompose(true);
        this.controllerEngineInitialize.initialize();

        // 初始化周期服務模塊
        this.periodEngineInitialize.initialize();

        message = "------------------------Manager Compose Initialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

}
