package cn.foxtech.iot.fox.publish.service.scheduler;

import cn.foxtech.common.entity.entity.DeviceRecordEntity;
import cn.foxtech.common.entity.entity.OperateRecordEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.fox.publish.service.entity.PeriodRecordEntity;
import cn.foxtech.iot.fox.publish.service.publish.RecordEntityPublish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MySqlEntityPushScheduler extends PeriodTaskService {
    @Autowired
    private RecordEntityPublish recordEntityPublish;

    @Autowired
    private RemoteMqttService remoteMqttService;

    @Override
    public void execute(long threadId) throws InterruptedException {
        Thread.sleep(1 * 1000);

        if (!this.remoteMqttService.getClient().isConnected()) {
            return;
        }

        this.recordEntityPublish.publish(DeviceRecordEntity.class.getSimpleName(), "tb_device_record");
        this.recordEntityPublish.publish(PeriodRecordEntity.class.getSimpleName(), "tb_period_record");
        this.recordEntityPublish.publish(OperateRecordEntity.class.getSimpleName(), "tb_operate_record");
    }
}
