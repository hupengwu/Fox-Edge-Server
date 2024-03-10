package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.fox.cloud.common.service.RemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecordEntityManageScheduler extends PeriodTaskService {
    /**
     * 边缘服务器ID
     */
    private final String edgeId = OSInfoUtils.getCPUID();

    /**
     * 实体管理者
     */
    @Autowired
    private LoggerEntitySynchronizer loggerEntitySynchronizer;

    /**
     * 云端发布者
     */
    @Autowired
    private RecordEntitySynchronizer recordEntitySynchronizer;

    @Autowired
    private RemoteService remoteService;


    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 云端是否处于锁定状态
        if (!this.remoteService.isLogin() && this.remoteService.isLockdown()) {
            return;
        }

        Thread.sleep(10 * 1000);
        this.recordEntitySynchronizer.syncEntity(edgeId);

        Thread.sleep(10 * 1000);
        this.loggerEntitySynchronizer.syncEntity(edgeId);
    }
}
