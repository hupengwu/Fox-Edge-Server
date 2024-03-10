package cn.foxtech.iot.fox.cloud.publisher.initialize;


import cn.foxtech.iot.fox.cloud.publisher.ConfigEntityManageScheduler;
import cn.foxtech.iot.fox.cloud.publisher.DefineEntityManageScheduler;
import cn.foxtech.iot.fox.cloud.publisher.RecordEntityManageScheduler;
import cn.foxtech.iot.fox.cloud.publisher.ValueEntityManageScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializePublisher {
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
