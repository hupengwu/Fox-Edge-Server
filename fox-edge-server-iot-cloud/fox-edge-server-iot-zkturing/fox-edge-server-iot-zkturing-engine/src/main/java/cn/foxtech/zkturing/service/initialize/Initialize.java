package cn.foxtech.zkturing.service.initialize;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.zkturing.service.scheduler.DeviceRecordPushScheduler;
import cn.foxtech.zkturing.service.scheduler.DeviceValuePushScheduler;
import cn.foxtech.zkturing.service.service.ZKTuringlService;
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
    private DeviceValuePushScheduler deviceValuePushScheduler;

    @Autowired
    private DeviceRecordPushScheduler deviceRecordPushScheduler;

    @Autowired
    private ZKTuringlService turinglService;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 初始化公共组件
        this.initializeCommon.getEntityManageService().addConsumer(ConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(ExtendConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(IotDeviceModelEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceStatusEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addAgileConsumer(DeviceValueEntity.class.getSimpleName());
        this.initializeCommon.initialize();

        // 初始化华为组件
        this.turinglService.initialize();

        // 设备信息的推送
        this.deviceValuePushScheduler.schedule();

        this.deviceRecordPushScheduler.schedule();


        logger.info("------------------------初始化结束！------------------------");
    }
}
