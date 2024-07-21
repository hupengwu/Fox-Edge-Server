package cn.foxtech.iot.fox.publish.service.scheduler;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.fox.publish.service.publish.ConfigEntityPublish;
import cn.foxtech.iot.fox.publish.service.publish.ValueEntityPublish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DeviceValueEntity
 * <p>
 * 策略：根据DeviceValue内容变更而触发通知，
 */
@Component
public class RedisEntityPushScheduler extends PeriodTaskService {
    @Autowired
    private ValueEntityPublish valueEntityPublish;

    @Autowired
    private ConfigEntityPublish configEntityPublish;

    @Autowired
    private RemoteMqttService remoteMqttService;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1 * 1000);

        if (!this.remoteMqttService.getClient().isConnected()) {
            return;
        }

        // 高频变化的设备数值类实体
        this.valueEntityPublish.publish(DeviceValueEntity.class.getSimpleName());
        this.valueEntityPublish.publish(DeviceValueExEntity.class.getSimpleName());

        // 不会经常变化的配置类型的实体
        this.configEntityPublish.publish(ConfigEntity.class.getSimpleName());
        this.configEntityPublish.publish(ChannelEntity.class.getSimpleName());
        this.configEntityPublish.publish(ChannelStatusEntity.class.getSimpleName());
        this.configEntityPublish.publish(DeviceEntity.class.getSimpleName());
        this.configEntityPublish.publish(DeviceMapperEntity.class.getSimpleName());
        this.configEntityPublish.publish(DeviceModelEntity.class.getSimpleName());
        this.configEntityPublish.publish(DeviceStatusEntity.class.getSimpleName());
        this.configEntityPublish.publish(DeviceValueExTaskEntity.class.getSimpleName());
        this.configEntityPublish.publish(ExtendConfigEntity.class.getSimpleName());
        this.configEntityPublish.publish(IotDeviceModelEntity.class.getSimpleName());
        this.configEntityPublish.publish(OperateChannelTaskEntity.class.getSimpleName());
        this.configEntityPublish.publish(OperateEntity.class.getSimpleName());
        this.configEntityPublish.publish(OperateManualTaskEntity.class.getSimpleName());
        this.configEntityPublish.publish(OperateMonitorTaskEntity.class.getSimpleName());
        this.configEntityPublish.publish(RepoCompEntity.class.getSimpleName());
    }
}
