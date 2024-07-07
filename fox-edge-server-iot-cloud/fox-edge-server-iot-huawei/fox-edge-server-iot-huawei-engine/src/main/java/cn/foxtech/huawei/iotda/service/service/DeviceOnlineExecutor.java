package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.service.entity.event.EventUp;
import cn.foxtech.huawei.iotda.service.entity.event.EventUpBuilder;
import cn.foxtech.huawei.iotda.service.huawei.HuaweiIoTDAService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceOnlineExecutor {
    private final Map<String, Boolean> statusMap = new ConcurrentHashMap<>();
    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private DeviceExecutor deviceExecutor;
    @Autowired
    private HuaweiIoTDAService huaweiIoTDAService;

    /**
     * 通知华为云，在网关设备下，添加子设备
     */
    public void pushDeviceStatus(boolean force) {
        // 获得华为物联网相关的设备
        Map<String, DeviceEntity> key2Entity = this.deviceExecutor.getKey2Entity();

        // 获得设备的在线状态
        Map<String, Boolean> pushStatusMap = new HashMap<>();
        Map<String, Boolean> newStatusMap = this.getDeviceStatus(key2Entity);
        for (String key : newStatusMap.keySet()) {
            Boolean newStatus = newStatusMap.get(key);

            // 检查：缓存中，是否存在该设备状态，如果没有，那么就保持新状态并推送
            Boolean status = this.statusMap.get(key);
            if (status == null) {
                this.statusMap.put(key, newStatus);
                pushStatusMap.put(key, newStatus);
                continue;
            }

            // 检查：缓存中，是否发生了状态变化，如果变化了，那么就保持新状态并推送
            if (!status.equals(newStatus)) {
                this.statusMap.put(key, newStatus);
                pushStatusMap.put(key, newStatus);
                continue;
            }
        }

        if (force){
            pushStatusMap.putAll(newStatusMap);
        }

        // 检查：是否有需要推送的数据
        if (pushStatusMap.isEmpty()) {
            return;
        }

        // 推送设备在线状态
        this.pushDeviceStatus(pushStatusMap, key2Entity);
    }


    private void pushDeviceStatus(Map<String, Boolean> statusMap, Map<String, DeviceEntity> key2Entity) {
        // 分批发送
        List<List<String>> pages = SplitUtils.split(statusMap.keySet(), 40);
        for (List<String> page : pages) {
            String productId = this.huaweiIoTDAService.getProductId();
            String nodeId = this.huaweiIoTDAService.getNodeId();
            String deviceId = this.huaweiIoTDAService.getDeviceId();

            // 转换为map结构
            Map<Long, Boolean> pageStatus = new HashMap<>();
            for (String key : page) {
                DeviceEntity entity = key2Entity.get(key);
                Boolean status = statusMap.get(key);
                if (status == null || entity == null) {
                    continue;
                }

                pageStatus.put(entity.getId(), status);
            }


            // 生成子设备的状态更新事件
            String eventId = UuidUtils.randomUUID();
            EventUp event = EventUpBuilder.sub_device_update_status(productId, pageStatus, eventId);

            // 转换为JSON报文
            String body = JsonUtils.buildJsonWithoutException(event);

            // 对应的topic
            String topic = EventUpBuilder.getTopic(nodeId);

            // 发送消息
            this.remoteMqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Map<String, Boolean> getDeviceStatus(Map<String, DeviceEntity> key2Entity) {
        Map<String, Boolean> statusMap = new HashMap<>();
        Long time = System.currentTimeMillis();
        DeviceStatusEntity find = new DeviceStatusEntity();
        for (String key : key2Entity.keySet()) {
            DeviceEntity deviceEntity = key2Entity.get(key);
            find.setId(deviceEntity.getId());

            DeviceStatusEntity deviceStatusEntity = this.entityManageService.getEntity(find.makeServiceKey(), DeviceStatusEntity.class);
            if (deviceStatusEntity == null) {
                continue;
            }

            Long commSuccessTime = deviceStatusEntity.getCommSuccessTime();
            if (commSuccessTime == null) {
                continue;
            }

            statusMap.put(key, time - commSuccessTime < this.huaweiIoTDAService.getDeviceOnlinePush() * 60 * 1000L);
        }

        return statusMap;
    }
}
