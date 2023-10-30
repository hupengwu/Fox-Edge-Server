package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.common.mqtt.MqttService;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import cn.foxtech.huawei.iotda.service.entity.event.EventsUpBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddSubDeviceExecutor {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private MqttService mqttService;

    public void addSubDeviceRequest() {
        List<BaseEntity> deviceEntityList = this.entityManageService.getEntityList(DeviceEntity.class);
        List<Long> deviceIds = ContainerUtils.buildListByGetField(deviceEntityList, BaseEntity::getId, Long.class);
        List<List<Long>> pages = SplitUtils.split(deviceIds, 40);

        for (List<Long> page : pages) {
            String productId = this.mqttService.getProductId();
            String eventId = UuidUtils.randomUUID();
            EventsUpBuilder.add_sub_device_request(productId, page, eventId);
        }
    }
}
