package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.constant.DeviceValueVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.common.mqtt.MqttService;
import cn.foxtech.huawei.iotda.common.service.EntityManageService;
import cn.foxtech.huawei.iotda.service.entity.property.subdev.SubDevPropertyReport;
import cn.foxtech.huawei.iotda.service.entity.property.subdev.SubDevPropertyReportBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SubDevPropertyExecutor {
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
        List<String> deviceKeys = this.subDeviceExecutor.getDeviceKeys();

        // 获得设备的在线状态
        Map<Long, Map<String, Object>> deviceValueMap = this.getDeviceValues(deviceKeys);


        // 分批发送
        List<List<Long>> pages = SplitUtils.split(deviceValueMap.keySet(), 40);
        for (List<Long> page : pages) {
            String productId = this.mqttService.getProductId();
            String nodeId = this.mqttService.getNodeId();
            String deviceId = this.mqttService.getDeviceId();

            // 转换为map结构
            Map<Long, Map<String, Object>> valuePage = new HashMap<>();
            for (Long subDeviceId : page) {
                Map<String, Object> values = deviceValueMap.get(subDeviceId);
                if (values == null) {
                    continue;
                }

                valuePage.put(subDeviceId, values);
            }


            // 生成子设备的状态更新事件
            String eventId = UuidUtils.randomUUID();
            SubDevPropertyReport event = SubDevPropertyReportBuilder.sub_devices_property_report_request(productId, "analog", valuePage);

            // 转换为JSON报文
            String body = JsonUtils.buildJsonWithoutException(event);

            // 对应的topic
            String topic = SubDevPropertyReportBuilder.getTopic(nodeId);

            // 发送消息
            this.mqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
        }
    }

    public Map<Long, Map<String, Object>> getDeviceValues(List<String> deviceNames) {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class);
        Map<String, Object> deviceValueMap = redisReader.readHashMap();
        if (deviceValueMap == null) {
            return new HashMap<>();
        }

        Map<Long, Map<String, Object>> deviceValue = new HashMap<>();

        DeviceEntity find = new DeviceEntity();
        for (String deviceName : deviceNames) {
            find.setDeviceName(deviceName);

            DeviceEntity deviceEntity = this.entityManageService.getEntity(find.makeServiceKey(), DeviceEntity.class);
            if (deviceEntity == null) {
                continue;
            }


            Map<String, Object> deviceMap = (Map<String, Object>) deviceValueMap.get(find.makeServiceKey());
            if (deviceMap == null) {
                continue;
            }

            Map<String, Object> params = (Map<String, Object>) deviceMap.get(DeviceValueVOFieldConstant.field_params);
            if (params == null) {
                continue;
            }

            // 提取出数值
            Map<String, Object> values = new HashMap<>();
            for (String key : params.keySet()) {
                Map<String, Object> map = (Map<String, Object>) params.get(key);
                Object value = map.get(DeviceValueVOFieldConstant.field_value_value);
                if (value == null) {
                    continue;
                }

                values.put(key, 10);
            }


            deviceValue.put(deviceEntity.getId(), values);
        }

        return deviceValue;
    }
}
