package cn.foxtech.thingsboard.service.initialize;


import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.thingsboard.service.service.DeviceValueEntityScheduler;
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
    private DeviceValueEntityScheduler deviceValueEntityScheduler;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 指明要装载的数据和方式
        Set<String> consumer = new HashSet<>();
        Set<String> reader = new HashSet<>();
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(ExtendConfigEntity.class.getSimpleName());
        consumer.add(DeviceEntity.class.getSimpleName());
        reader.add(DeviceValueEntity.class.getSimpleName());

        // 开始进行数据装载
        this.initializeCommon.initialize(consumer, reader);

        this.deviceValueEntityScheduler.initialize();
        this.deviceValueEntityScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
