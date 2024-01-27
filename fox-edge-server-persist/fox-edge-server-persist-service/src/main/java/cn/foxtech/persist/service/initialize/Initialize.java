package cn.foxtech.persist.service.initialize;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.persist.common.initialize.PersistInitialize;
import cn.foxtech.persist.service.scheduler.PeriodTaskScheduler;
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
    private PeriodTaskScheduler periodTaskScheduler;


    @Autowired
    private PersistInitialize persistInitialize;

    @Autowired
    private InitialConfigService configService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 装载数据实体，并启动同步线程
        this.persistInitialize.initialize();

        // 初始化全局配置参数
        this.configService.initialize("serverConfig", "serverConfig.json");

        // 设备记录的上报接收任务
        this.periodTaskScheduler.schedule();


        // 在启动阶段，会产生很多临时数据，所以强制GC一次
        System.gc();

        logger.info("------------------------初始化结束！------------------------");
    }
}
