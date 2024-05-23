package cn.foxtech.thingspanel.service.initialize;


import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.thingspanel.service.service.DeviceValueEntityScheduler;
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
    private DeviceValueEntityScheduler deviceValueEntityScheduler;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 开始进行数据装载
        this.initializeCommon.getEntityManageService().addConsumer(ConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(ExtendConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addReader(DeviceValueEntity.class.getSimpleName());
        this.initializeCommon.initialize();

        this.deviceValueEntityScheduler.initialize();
        this.deviceValueEntityScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
