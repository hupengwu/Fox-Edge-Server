package cn.foxtech.controller.common.initialize;

import cn.foxtech.controller.common.scheduler.EntityManageScheduler;
import cn.foxtech.controller.common.service.EntityManageService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Data
@Component
public class ControllerInitialize {
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

    @Value("${spring.redis_topic.controller_model}")
    private String controllerModel = "system_controller";

    /**
     * 进程状态
     */
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;


    public void initialize() {
        // 进程状态
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 装载数据实体，并启动同步线程
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();
        this.entityManageScheduler.schedule();
    }
}
