package cn.foxtech.persist.common.initialize;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.persist.common.scheduler.EntityManageScheduler;
import cn.foxtech.persist.common.service.DeviceObjectMapper;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.persist.common.service.EntityVerifyService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Data
@Component
public class PersistInitialize {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 实体调度
     */
    @Autowired
    private EntityManageScheduler entityManageScheduler;

    /**
     * 进程状态
     */
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    @Autowired
    private EntityVerifyService entityVerifyService;

    public void initialize() {
        // 进程状态
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 注册
        this.entityManageService.initialize();
        // mysql和redis之间的数据验证
        this.entityVerifyService.initialize();

        // 装载数据
        this.entityManageService.initLoadEntity();
        this.entityManageScheduler.schedule();

        // 同步映射数据
        this.deviceObjectMapper.syncEntity();
    }
}
