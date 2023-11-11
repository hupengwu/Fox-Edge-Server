package cn.foxtech.manager.system.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.manager.common.initialize.CommonInitialize;
import cn.foxtech.manager.system.scheduler.EntityManageScheduler;
import cn.foxtech.manager.system.scheduler.PeriodTasksScheduler;
import cn.foxtech.manager.system.scheduler.TopicManagerScheduler;
import cn.foxtech.manager.system.service.EntityManageService;
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
    private CommonInitialize commonInitialize;

    @Autowired
    private TopicManagerScheduler topicManagerScheduler;

    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    public void run(String... args) {
        this.logger.info("------------------------SystemInitialize初始化开始！------------------------");

        this.commonInitialize.initialize();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        // topic响应
        this.topicManagerScheduler.schedule();

        // 启动周期性任务
        this.periodTasksScheduler.initialize();
        this.periodTasksScheduler.schedule();

        this.logger.info("------------------------SystemInitialize初始化结束！------------------------");
    }
}
