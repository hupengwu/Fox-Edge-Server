package cn.foxtech.iot.fox.publish.service.initialize;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.iot.fox.publish.service.scheduler.MySqlEntityPushScheduler;
import cn.foxtech.iot.fox.publish.service.scheduler.RedisEntityPushScheduler;
import cn.foxtech.iot.fox.publish.service.service.IotFoxPublishService;
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
    private RedisEntityPushScheduler redisEntityPushScheduler;

    @Autowired
    private MySqlEntityPushScheduler mySqlEntityPushScheduler;

    @Autowired
    private IotFoxPublishService iotFoxPublishService;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 初始化公共组件
        this.initializeCommon.getEntityManageService().addConsumer(ConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(ExtendConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(IotDeviceModelEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceStatusEntity.class.getSimpleName());
        this.initializeCommon.initialize();

        // 初始化华为组件
        this.iotFoxPublishService.initialize();

        // 设备信息的推送
        this.redisEntityPushScheduler.schedule();

        this.mySqlEntityPushScheduler.schedule();


        logger.info("------------------------初始化结束！------------------------");
    }
}