package cn.foxtech.huawei.iotda.service.initialize;


import cn.foxtech.common.entity.entity.*;
import cn.foxtech.huawei.iotda.service.huawei.HuaweiIoTDAService;
import cn.foxtech.huawei.iotda.service.service.DevicePushScheduler;
import cn.foxtech.huawei.iotda.service.service.DevValueExecutor;
import cn.foxtech.huawei.iotda.service.service.DeviceOnlineExecutor;
import cn.foxtech.huawei.iotda.service.service.DeviceRegisterExecutor;
import cn.foxtech.iot.common.initialize.InitializeCommon;
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
    private DevicePushScheduler devicePushScheduler;

    @Autowired
    private DeviceRegisterExecutor deviceRegisterExecutor;

    @Autowired
    private DeviceOnlineExecutor deviceOnlineExecutor;

    @Autowired
    private DevValueExecutor devValueExecutor;

    @Autowired
    private HuaweiIoTDAService huaweiIoTDAService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 指明要装载的数据和方式：对ConfigEntity/ExtendConfigEntity/DeviceEntity会使用到本地缓存，对DeviceValueEntity直读redis
        Set<String> consumer = new HashSet<>();
        Set<String> reader = new HashSet<>();
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(ExtendConfigEntity.class.getSimpleName());
        consumer.add(DeviceEntity.class.getSimpleName());
        consumer.add(DeviceStatusEntity.class.getSimpleName());
        reader.add(DeviceValueEntity.class.getSimpleName());

        // 初始化公共组件
        this.initializeCommon.initialize(consumer, reader);

        // 初始化华为组件
        this.huaweiIoTDAService.initialize();

        // 设备信息的推送
        this.devicePushScheduler.initialize();
        this.devicePushScheduler.schedule();

      //      this.subDeviceOnlineExecutor.subDeviceUpdateStatus();
      //  this.subDevPropertyExecutor.subDevPropertyReportRequest();

        logger.info("------------------------初始化结束！------------------------");
    }
}
