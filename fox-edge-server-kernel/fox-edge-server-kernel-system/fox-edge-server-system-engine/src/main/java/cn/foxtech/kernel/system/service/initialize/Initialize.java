package cn.foxtech.kernel.system.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.common.initialize.CommonInitialize;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.repository.initialize.RepoInitialize;
import cn.foxtech.kernel.system.service.restfullike.mqtt.MqttRestfulLikeService;
import cn.foxtech.kernel.system.service.restfullike.redis.RedisRestfulLikeController;
import cn.foxtech.kernel.system.service.restfullike.redis.RedisRestfulLikeScheduler;
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

    @Autowired
    private EdgeService edgeService;

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
    private MqttRestfulLikeService mqttRestfulLikeService;

    @Autowired
    private RedisRestfulLikeController redisRestfulLikeController;


    @Autowired
    private RedisRestfulLikeScheduler redisRestfulLikeScheduler;

    public void run(String... args) {
        String message = "------------------------Initialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.commonInitialize.initialize();

        this.repoInitialize.initialize();

        // RestfulLike的mqtt和redis的消息响应
        this.mqttRestfulLikeService.initialize();
        this.redisRestfulLikeController.initialize();
        this.redisRestfulLikeScheduler.schedule();

        this.createPeriodTask();

        message = "------------------------Initialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

    private void createPeriodTask() {
        // 检查：只有在非docker模式下，才能进行下列操作
        if (!this.edgeService.isDockerEnv()) {
            this.periodTasksScheduler.insertPeriodTask(this.cleanCacheTask);
        }

        this.periodTasksScheduler.insertPeriodTask(this.cleanLogFileTask);
        this.periodTasksScheduler.insertPeriodTask(this.configEntityTask);
    }

}
