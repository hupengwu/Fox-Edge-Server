package cn.foxtech.iot.common.initialize;


import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.iot.common.remote.RemoteProxyService;
import cn.foxtech.iot.common.scheduler.EntityManageScheduler;
import cn.foxtech.iot.common.service.EntityManageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializeCommon {
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Getter
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private RemoteProxyService remoteProxyService;

    /**
     * 请在调用该方法之前，使用entityManageService的
     */
    public void initialize() {
        // 初始化进程的状态：通告本身服务的信息给其他服务addConsumer()，addReader()，指定数据的访问方式
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 装载数据实体
        this.entityManageService.initLoadEntity();

        // 将全局配置，读取到本地缓存中，方便后面反复使用，该方法必须在this.entityManageService.initLoadEntity()之后执行
        this.localConfigService.initialize();

        // 远程通信组件的初始化
        this.remoteProxyService.initialize();

        // 启动同步线程
        this.entityManageScheduler.schedule();
    }
}
