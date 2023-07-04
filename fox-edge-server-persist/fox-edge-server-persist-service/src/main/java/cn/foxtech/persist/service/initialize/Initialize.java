package cn.foxtech.persist.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.persist.common.initialize.PersistInitialize;
import cn.foxtech.persist.service.controller.ManagerController;
import cn.foxtech.persist.service.controller.PersistController;
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
    private PersistController persistController;

    @Autowired
    private ManagerController managerController;


    @Autowired
    private PersistInitialize persistInitialize;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        ProcessUtils.killLoader();

        // 装载数据实体，并启动同步线程
        this.persistInitialize.initialize();

        // 调度设备上报收集任务
        this.persistController.schedule();
        // 调度管理服务
        this.managerController.schedule();

        // 在启动阶段，会产生很多临时数据，所以强制GC一次
        System.gc();

        logger.info("------------------------初始化结束！------------------------");
    }
}
