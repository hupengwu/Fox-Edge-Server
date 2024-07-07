package cn.foxtech.kernel.system.common.initialize;


import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.common.initialize.KernelInitialize;
import cn.foxtech.kernel.system.common.scheduler.EntityManageScheduler;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.common.task.GateWayRouteUpdateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class CommonInitialize {
    private static final Logger logger = LoggerFactory.getLogger(CommonInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private KernelInitialize kernelInitialize;


    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Autowired
    private GateWayRouteUpdateTask gateWayRouteUpdateTask;

    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService configService;


    @Value("${spring.fox-service.mode.router}")
    private String routerMode;


    public void initialize() {
        String message = "------------------------SystemInitialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.kernelInitialize.initialize();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        // 初始化通信超时配置
        this.configService.initialize("deviceTimeOutConfig", "deviceTimeOutConfig.json");


        // 启动周期任务线程
        this.periodTasksScheduler.schedule();

        // 添加周期任务
        this.createPeriodTask();

        message = "------------------------SystemInitialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

    private void createPeriodTask() {
        // 检查：本地工作模式下，向gateway服务手动注册路由
        if ("local".equals(this.routerMode)) {
            this.periodTasksScheduler.insertPeriodTask(this.gateWayRouteUpdateTask);
        }
    }
}
