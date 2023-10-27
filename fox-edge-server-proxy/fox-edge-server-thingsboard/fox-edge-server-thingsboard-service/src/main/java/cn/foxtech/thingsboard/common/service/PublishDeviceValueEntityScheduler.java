package cn.foxtech.thingsboard.common.service;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValueEntityManageScheduler extends PeriodTaskService {
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

        this.valueEntitySynchronizer.syncEntity();
        Thread.sleep(60 * 1000);
    }
}
