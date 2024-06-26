package cn.foxtech.kernel.system.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.system.common.initialize.CommonInitialize;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.common.scheduler.RedisListRestfulScheduler;
import cn.foxtech.kernel.system.repository.initialize.RepoInitialize;
import cn.foxtech.kernel.system.service.controller.RestfulLikeController;
import cn.foxtech.kernel.system.service.mqtt.MqttProxyService;
import cn.foxtech.kernel.system.service.redislist.RedisListRestfulHandler;
import cn.foxtech.kernel.system.service.redistopic.RedisTopicController;
import cn.foxtech.kernel.system.service.task.CleanCacheTask;
import cn.foxtech.kernel.system.service.task.CleanLogFileTask;
import cn.foxtech.kernel.system.service.task.ConfigEntityTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Initialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;


    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;


    @Autowired
    private CommonInitialize commonInitialize;

    @Autowired
    private RepoInitialize repoInitialize;

    /**
     * 定时清理操作系统的Cache
     */
    @Autowired
    private CleanCacheTask cleanCacheTask;

    @Autowired
    private ConfigEntityTask configEntityTask;

    /**
     * 删除失效链路任务
     */
    @Autowired
    private CleanLogFileTask cleanLogFileTask;

    @Autowired
    private MqttProxyService mqttProxyService;

    @Autowired
    private RedisListRestfulScheduler restfulScheduler;

    @Autowired
    private RedisListRestfulHandler restfulHandler;

    @Autowired
    private RedisTopicController redisTopicController;

    @Autowired
    private RestfulLikeController restfulController;

    public void run(String... args) {
        String message = "------------------------Initialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.commonInitialize.initialize();

        this.repoInitialize.initialize();

        this.mqttProxyService.initialize();

        this.redisTopicController.initialize();

        this.restfulController.initialize();

        this.restfulScheduler.setHandler(this.restfulHandler);
        this.restfulScheduler.schedule();

        this.createPeriodTask();

        message = "------------------------Initialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

    private void createPeriodTask() {
        this.periodTasksScheduler.insertPeriodTask(this.cleanCacheTask);
        this.periodTasksScheduler.insertPeriodTask(this.cleanLogFileTask);
        this.periodTasksScheduler.insertPeriodTask(this.configEntityTask);
    }

}
