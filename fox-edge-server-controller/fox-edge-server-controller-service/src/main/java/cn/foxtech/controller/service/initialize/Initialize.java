package cn.foxtech.controller.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.controller.common.initialize.ControllerInitialize;
import cn.foxtech.controller.service.service.CollectorExchangeService;
import cn.foxtech.controller.service.service.CollectorSubscribeService;
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
    private CollectorExchangeService exchangeService;

    @Autowired
    private CollectorSubscribeService subscribeService;


    @Autowired
    private ControllerInitialize controllerInitialize;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        ProcessUtils.killLoader();

        this.controllerInitialize.initialize();

        // 调度设备数据采集任务
        this.exchangeService.schedule();

        // 调度设备上报收集任务
        this.subscribeService.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
