package cn.foxtech.device.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.device.service.controller.DeviceExecuteController;
import cn.foxtech.device.service.controller.DeviceReportController;
import cn.foxtech.device.service.scheduler.EntityManageScheduler;
import cn.foxtech.device.service.service.EntityManageService;
import cn.foxtech.device.service.service.MethodEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;


    @Autowired
    private DeviceExecuteController deviceExecuteController;

    @Autowired
    private DeviceReportController deviceReportController;

    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private MethodEntityService methodEntityService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 从数据库和redis中，装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 从第三方jar扫描解码器，并生成redis记录
        this.methodEntityService.scanJarFile();
        this.methodEntityService.updateEntityList();

        this.entityManageScheduler.schedule();

        // 启动对客户端的响应线程
        this.deviceExecuteController.schedule(3);

        this.deviceReportController.schedule();


        logger.info("------------------------初始化完成！------------------------");
    }
}
