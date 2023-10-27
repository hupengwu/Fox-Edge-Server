package cn.foxtech.thingsboard.service.service;

import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PublishDeviceValueEntityScheduler extends PeriodTaskService {
    /**
     * 云端发布者
     */
    @Autowired
    private PublishDeviceValueEntitySynchronizer publishDeviceValueEntitySynchronizer;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        this.publishDeviceValueEntitySynchronizer.syncEntity();
    }
}
