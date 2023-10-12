package cn.foxtech.proxy.cloud.publisher.initialize;


import cn.foxtech.proxy.cloud.common.service.ConfigManageService;
import cn.foxtech.proxy.cloud.publisher.ConfigEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.DefineEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.RecordEntityManageScheduler;
import cn.foxtech.proxy.cloud.publisher.ValueEntityManageScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializePublisher {
    @Autowired
    private ConfigManageService configManageService;

    @Autowired
    private ConfigEntityManageScheduler configEntityManageScheduler;


    @Autowired
    private RecordEntityManageScheduler recordEntityManageScheduler;

    @Autowired
    private DefineEntityManageScheduler defineEntityManageScheduler;

    @Autowired
    private ValueEntityManageScheduler valueEntityManageScheduler;


    public void initialize() {
        // 启动同步线程
        this.configEntityManageScheduler.schedule();

        this.recordEntityManageScheduler.schedule();

        this.defineEntityManageScheduler.schedule();

        this.valueEntityManageScheduler.schedule();
    }
}
