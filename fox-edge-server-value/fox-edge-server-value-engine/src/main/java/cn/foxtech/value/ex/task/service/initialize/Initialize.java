package cn.foxtech.value.ex.task.service.initialize;


import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.entity.entity.DeviceValueExTaskEntity;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.value.ex.task.service.notify.DeviceEntityTypeNotify;
import cn.foxtech.value.ex.task.service.notify.DeviceMapperTypeNotify;
import cn.foxtech.value.ex.task.service.notify.VauleTaskTypeNodify;
import cn.foxtech.value.ex.task.service.scheduler.EntityManageScheduler;
import cn.foxtech.value.ex.task.service.scheduler.PeriodTasksScheduler;
import cn.foxtech.value.ex.task.service.service.EntityManageService;
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
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private DeviceMapperTypeNotify deviceMapperNotify;

    @Autowired
    private DeviceEntityTypeNotify deviceEntityNotify;

    @Autowired
    private VauleTaskTypeNodify valueTaskNodify;


    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 状态发布
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 从数据库和redis中，装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        ConsumerRedisService consumerRedisService;
        consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(DeviceEntity.class);
        consumerRedisService.bind(this.deviceEntityNotify);
        consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(DeviceMapperEntity.class);
        consumerRedisService.bind(this.deviceMapperNotify);
        consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(DeviceValueExTaskEntity.class);
        consumerRedisService.bind(this.valueTaskNodify);

        // 启动各Entity的数据同步
        this.entityManageScheduler.schedule();

        this.periodTasksScheduler.initialize();
        this.periodTasksScheduler.schedule();

        // 在启动阶段，会产生很多临时数据，所以强制GC一次
        System.gc();

        logger.info("------------------------初始化完成！------------------------");
    }
}
