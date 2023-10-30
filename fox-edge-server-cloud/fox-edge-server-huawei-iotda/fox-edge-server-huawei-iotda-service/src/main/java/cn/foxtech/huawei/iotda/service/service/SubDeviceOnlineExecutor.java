package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.constant.DeviceStatusVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.common.mqtt.MqttService;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import cn.foxtech.huawei.iotda.service.entity.event.EventUp;
import cn.foxtech.huawei.iotda.service.entity.event.EventUpBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SubDeviceOnlineExecutor {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private SubDeviceExecutor subDeviceExecutor;

    /**
     * 通知华为云，在网关设备下，添加子设备
     */
    public void subDeviceUpdateStatus() {
        // 获得华为物联网相关的设备
        List<Long> deviceIds = this.subDeviceExecutor.getDeviceIds();

        // 获得设备的在线状态
        Map<Long, Boolean> statusMap = this.getDeviceStatus(deviceIds);


        // 分批发送
        List<List<Long>> pages = SplitUtils.split(deviceIds, 40);
        for (List<Long> page : pages) {
            String productId = this.mqttService.getProductId();
            String nodeId = this.mqttService.getNodeId();
            String deviceId = this.mqttService.getDeviceId();

            // 转换为map结构
            Map<Long, Boolean> pageStatus = new HashMap<>();
            for (Long subDeviceId : page) {
                Boolean status = statusMap.get(subDeviceId);
                if (status == null) {
                    continue;
                }

                pageStatus.put(subDeviceId, status);
            }


            // 生成子设备的状态更新事件
            String eventId = UuidUtils.randomUUID();
            EventUp event = EventUpBuilder.sub_device_update_status(productId, pageStatus, eventId);

            // 转换为JSON报文
            String body = JsonUtils.buildJsonWithoutException(event);

            // 对应的topic
            String topic = EventUpBuilder.getTopic(nodeId);

            // 发送消息
            this.mqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
        }
    }

    public Map<Long, Boolean> getDeviceStatus(List<Long> deviceIds) {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceStatusEntity.class);
        Map<String, Object> deviceStatusMap = redisReader.readHashMap();
        if (deviceStatusMap == null) {
            return new HashMap<>();
        }

        Map<Long, Boolean> statusMap = new HashMap<>();
        Long time = System.currentTimeMillis();
        DeviceStatusEntity deviceStatusEntity = new DeviceStatusEntity();
        for (Long deviceId : deviceIds) {
            deviceStatusEntity.setId(deviceId);

            Map<String, Object> map = (Map<String, Object>) deviceStatusMap.get(deviceStatusEntity.makeServiceKey());
            if (map == null) {
                continue;
            }

            Long commSuccessTime = (Long) map.get(DeviceStatusVOFieldConstant.field_success_time);
            if (commSuccessTime == null) {
                continue;
            }

            statusMap.put(deviceId, time - commSuccessTime < this.mqttService.getDeviceOnlinePush() * 60 * 1000L);
        }

        return statusMap;
    }
}
