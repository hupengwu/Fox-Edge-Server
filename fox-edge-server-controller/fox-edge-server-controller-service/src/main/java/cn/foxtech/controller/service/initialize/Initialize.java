package cn.foxtech.controller.service.initialize;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.controller.common.initialize.ControllerInitialize;
import cn.foxtech.controller.service.scheduler.RedisListRespondScheduler;
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

    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService initialConfigService;


    @Autowired
    private RedisListRespondScheduler redisListRespondScheduler;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.controllerInitialize.initialize();

        this.initialConfigService.initialize("serverConfig", "serverConfig.json");

        // 调度设备数据采集任务
        this.exchangeService.schedule();

        // 调度设备上报收集任务
        this.subscribeService.schedule();

        this.redisListRespondScheduler.initialize();
        this.redisListRespondScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
