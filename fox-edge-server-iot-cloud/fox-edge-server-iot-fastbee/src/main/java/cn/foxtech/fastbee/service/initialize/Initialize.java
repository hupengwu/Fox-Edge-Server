/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.initialize;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.fastbee.service.scheduler.DeviceSubscribeScheduler;
import cn.foxtech.fastbee.service.scheduler.DeviceValuePushScheduler;
import cn.foxtech.fastbee.service.service.FastBeeService;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.iot.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);


    @Autowired
    private InitializeCommon initializeCommon;

    @Autowired
    private EntityManageService entityManageService;


    @Autowired
    private DeviceValuePushScheduler deviceValuePushScheduler;

    @Autowired
    private DeviceSubscribeScheduler deviceSubscribeScheduler;


    @Autowired
    private FastBeeService fastbeeService;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 初始化公共组件
        this.initializeCommon.getEntityManageService().addConsumer(ConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addReader(DeviceValueEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addReader(DeviceTemplateEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addReader(IotTemplateEntity.class.getSimpleName());

        this.initializeCommon.initialize();

        // 初始化华为组件
        this.fastbeeService.initialize();

        // 设备信息的推送
        this.deviceValuePushScheduler.schedule();
        this.deviceSubscribeScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
