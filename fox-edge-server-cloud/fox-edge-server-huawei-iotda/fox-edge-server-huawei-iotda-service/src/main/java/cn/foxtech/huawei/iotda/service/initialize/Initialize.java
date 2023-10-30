package cn.foxtech.huawei.iotda.service.initialize;


import cn.foxtech.huawei.iotda.common.initialize.InitializeCommon;
import cn.foxtech.huawei.iotda.service.service.DeviceValueEntityScheduler;
import cn.foxtech.huawei.iotda.service.service.SubDevPropertyExecutor;
import cn.foxtech.huawei.iotda.service.service.SubDeviceOnlineExecutor;
import cn.foxtech.huawei.iotda.service.service.SubDeviceRegisterExecutor;
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

    @Autowired
    private SubDeviceRegisterExecutor subDeviceRegisterExecutor;

    @Autowired
    private SubDeviceOnlineExecutor subDeviceOnlineExecutor;

    @Autowired
    private SubDevPropertyExecutor subDevPropertyExecutor;



    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.initializeCommon.initialize();

        this.deviceValueEntityScheduler.initialize();
        this.deviceValueEntityScheduler.schedule();

    //    this.subDeviceRegisterExecutor.addSubDeviceRequest();
   //     this.subDeviceOnlineExecutor.subDeviceUpdateStatus();
        this.subDevPropertyExecutor.subDeviceUpdateStatus();;

        logger.info("------------------------初始化结束！------------------------");
    }
}
