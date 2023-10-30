package cn.foxtech.huawei.iotda.common.initialize;


import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.huawei.iotda.common.mqtt.MqttService;
import cn.foxtech.huawei.iotda.common.scheduler.EntityManageScheduler;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializeCommon {
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private MqttService mqttService;

    public void initialize() {
        // 初始化进程的状态：通告本身服务的信息给其他服务
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        this.mqttService.initialize();
    }
}
