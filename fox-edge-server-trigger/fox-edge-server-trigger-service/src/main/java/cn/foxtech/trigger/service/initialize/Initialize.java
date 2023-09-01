package cn.foxtech.trigger.service.initialize;


import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.trigger.service.controller.ManagerController;
import cn.foxtech.trigger.service.scheduler.DeviceValueNotifyScheduler;
import cn.foxtech.trigger.service.scheduler.EntityManageScheduler;
import cn.foxtech.trigger.service.service.EntityManageService;
import cn.foxtech.trigger.service.service.MethodEntityService;
import cn.foxtech.trigger.service.trigger.TriggerValueUpdater;
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
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private DeviceValueNotifyScheduler deviceValueNotifyScheduler;

    @Autowired
    private TriggerValueUpdater triggerValueUpdater;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private ManagerController managerController;

    @Autowired
    private MethodEntityService methodEntityService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 状态发布
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 从数据库和redis中，装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 从第三方jar扫描解码器，并生成redis记录
        this.methodEntityService.scanJarFile();
        this.methodEntityService.updateEntityList();

        // 初始化TriggerValue
        this.triggerValueUpdater.initialize();

        // 启动各Entity的数据同步
        this.entityManageScheduler.schedule();


        this.deviceValueNotifyScheduler.schedule();

        this.managerController.schedule();

        // 在启动阶段，会产生很多临时数据，所以强制GC一次
        System.gc();

        logger.info("------------------------初始化完成！------------------------");
    }
}
