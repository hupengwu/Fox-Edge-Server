package cn.foxtech.iot.whzktl.service.initialize;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.iot.whzktl.service.scheduler.DeviceRecordPushScheduler;
import cn.foxtech.iot.whzktl.service.scheduler.DeviceValuePushScheduler;
import cn.foxtech.iot.whzktl.service.service.WhZktlIotService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);


    @Autowired
    private InitializeCommon initializeCommon;

    @Autowired
    private DeviceValuePushScheduler deviceValuePushScheduler;

    @Autowired
    private DeviceRecordPushScheduler deviceRecordPushScheduler;

    @Autowired
    private WhZktlIotService whZktlIotService;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 指明要装载的数据和方式：对ConfigEntity/ExtendConfigEntity/DeviceEntity会使用到本地缓存，对DeviceValueEntity直读redis
        Set<String> consumer = new HashSet<>();
        Set<String> reader = new HashSet<>();
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(ExtendConfigEntity.class.getSimpleName());
        consumer.add(IotDeviceModelEntity.class.getSimpleName());
        consumer.add(DeviceEntity.class.getSimpleName());
        consumer.add(DeviceStatusEntity.class.getSimpleName());
        consumer.add(DeviceValueEntity.class.getSimpleName());

        // 初始化公共组件
        this.initializeCommon.initialize(consumer, reader);

        // 初始化华为组件
        this.whZktlIotService.initialize();

        // 设备信息的推送
        this.deviceValuePushScheduler.schedule();

        this.deviceRecordPushScheduler.schedule();


        logger.info("------------------------初始化结束！------------------------");
    }
}
