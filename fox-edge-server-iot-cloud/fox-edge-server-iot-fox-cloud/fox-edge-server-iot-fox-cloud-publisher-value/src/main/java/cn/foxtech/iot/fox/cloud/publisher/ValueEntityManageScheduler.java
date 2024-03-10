package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.fox.cloud.common.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValueEntityManageScheduler extends PeriodTaskService {
    /**
     * 边缘服务器ID
     */
    private final String edgeId = OSInfoUtils.getCPUID();


    /**
     * 云端发布者
     */
    @Autowired
    private ValueEntitySynchronizer valueEntitySynchronizer;

    @Autowired
    private RemoteService remoteService;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 云端是否处于锁定状态
        if (!this.remoteService.isLogin() && this.remoteService.isLockdown()) {
            return;
        }

        this.valueEntitySynchronizer.syncEntity(edgeId);
        Thread.sleep(60 * 1000);
    }
}
