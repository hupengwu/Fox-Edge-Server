package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.entity.constant.DeviceValueVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.uuid.UuidUtils;
import cn.foxtech.huawei.iotda.service.entity.property.subdev.SubDevPropertyReport;
import cn.foxtech.huawei.iotda.service.entity.property.subdev.SubDevPropertyReportBuilder;
import cn.foxtech.huawei.iotda.service.entity.utils.ServiceIdUtils;
import cn.foxtech.huawei.iotda.service.huawei.HuaweiIoTDAService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DevValueExecutor {
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
    public void pushDeviceValue() {
        Map<String, Map<String, Object>> modelMap = this.huaweiIoTDAService.getModelMap();

        // 获得华为物联网相关的设备
        Map<String, DeviceEntity> key2Entity = this.deviceExecutor.getKey2Entity();

        // 获得设备的数值
        Map<String, Map<String, Object>> deviceValueMap = this.getDeviceValues(key2Entity, modelMap);

        // 剔除掉，华为物模型不支持的数据
        Map<String, Map<String, Object>> deviceValues = new HashMap<>();
        for (String key : deviceValueMap.keySet()) {
            DeviceEntity deviceEntity = key2Entity.get(key);
            if (deviceEntity == null) {
                continue;
            }

            // 取出对应的物模型
            Map<String, Object> model = modelMap.get(ServiceIdUtils.getServiceId(deviceEntity.getDeviceType()));
            if (model == null) {
                continue;
            }

            model.get(deviceEntity);

            deviceValues.put(key, deviceValueMap.get(key));
        }


        // 分批发送
        List<List<String>> pages = SplitUtils.split(deviceValues.keySet(), 40);
        for (List<String> page : pages) {
            String productId = this.huaweiIoTDAService.getProductId();
            String nodeId = this.huaweiIoTDAService.getNodeId();
            String deviceId = this.huaweiIoTDAService.getDeviceId();

            // 转换为map结构
            Map<String, Map<String, Object>> valuePage = new HashMap<>();
            for (String deviceKey : page) {
                Map<String, Object> values = deviceValues.get(deviceKey);
                if (values == null) {
                    continue;
                }


                valuePage.put(deviceKey, values);
            }


            // 生成子设备的状态更新事件
            String eventId = UuidUtils.randomUUID();
            SubDevPropertyReport event = SubDevPropertyReportBuilder.sub_devices_property_report_request(valuePage, productId, this.huaweiIoTDAService.getModelMap(), key2Entity);

            // 转换为JSON报文
            String body = JsonUtils.buildJsonWithoutException(event);

            // 对应的topic
            String topic = SubDevPropertyReportBuilder.getTopic(nodeId);

            // 发送消息
            this.remoteMqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
        }
    }

    private Map<String, Map<String, Object>> getDeviceValues(Map<String, DeviceEntity> key2Entity, Map<String, Map<String, Object>> modelMap) {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class);
        Map<String, Object> deviceValueMap = redisReader.readHashMap();
        if (deviceValueMap == null) {
            return new HashMap<>();
        }

        Map<String, Map<String, Object>> deviceValue = new HashMap<>();

        for (String deviceKey : key2Entity.keySet()) {
            Map<String, Object> deviceMap = (Map<String, Object>) deviceValueMap.get(deviceKey);
            if (deviceMap == null) {
                continue;
            }

            // 取出设备类型
            String deviceType = (String) deviceMap.get(DeviceValueVOFieldConstant.field_device_type);
            if (deviceType == null) {
                continue;
            }

            // 取出设备数值
            Map<String, Object> params = (Map<String, Object>) deviceMap.get(DeviceValueVOFieldConstant.field_params);
            if (params == null) {
                continue;
            }


            // 取出物模型
            Map<String, Object> properties = modelMap.get(ServiceIdUtils.getServiceId(deviceType));
            if (properties == null) {
                continue;
            }

            // 使用物模型，筛选范围内的数值
            Map<String, Object> values = new HashMap<>();
            for (String key : params.keySet()) {
                Map<String, Object> map = (Map<String, Object>) params.get(key);

                // 是否是物模型规定的数据
                if (!properties.containsKey(key)) {
                    continue;
                }

                // 取出数值
                Object value = map.get(DeviceValueVOFieldConstant.field_value_value);
                if (value == null) {
                    continue;
                }

                values.put(key, value);
            }

            // 检查：按物模型过滤后。是否还有可以上传的数据
            if (values.isEmpty()) {
                continue;
            }


            deviceValue.put(deviceKey, values);
        }

        return deviceValue;
    }
}
