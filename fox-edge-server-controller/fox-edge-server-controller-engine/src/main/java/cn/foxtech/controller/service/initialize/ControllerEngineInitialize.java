/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.controller.service.initialize;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.controller.common.initialize.ControllerInitialize;
import cn.foxtech.controller.service.service.CollectorExchangeService;
import cn.foxtech.controller.service.service.CollectorSubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class ControllerEngineInitialize {
    private static final Logger logger = LoggerFactory.getLogger(ControllerEngineInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private CollectorExchangeService exchangeService;

    @Autowired
    private CollectorSubscribeService subscribeService;


    @Autowired
    private ControllerInitialize controllerInitialize;

    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService configService;


    public void initialize() {
        String message = "------------------------ControllerEngine 初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.controllerInitialize.initialize();

        this.configService.initialize("serverConfig", "controllerServerConfig.json");

        // 调度设备数据采集任务
        this.exchangeService.schedule();

        // 调度设备上报收集任务
        this.subscribeService.schedule();

        message = "------------------------ControllerEngine 初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }
}
