package cn.foxtech.huawei.iotda.service.service;

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
import java.util.List;

@Component
public class SubDeviceRegisterExecutor {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private MqttService mqttService;

    @Autowired
    private SubDeviceExecutor subDeviceExecutor;

    /**
     * 通知华为云，在网关设备下，添加子设备
     */
    public void addSubDeviceRequest() {
        // 获得华为物联网平台相关的设备
        List<Long> deviceIds = this.subDeviceExecutor.getDeviceIds();

        // 分批发送
        List<List<Long>> pages = SplitUtils.split(deviceIds, 40);
        for (List<Long> page : pages) {
            String productId = this.mqttService.getProductId();
            String nodeId = this.mqttService.getNodeId();
            String deviceId = this.mqttService.getDeviceId();


            // 生成子设备注册事件
            String eventId = UuidUtils.randomUUID();
            EventUp event = EventUpBuilder.add_sub_device_request(productId, page, eventId);

            // 转换为JSON报文
            String body = JsonUtils.buildJsonWithoutException(event);

            // 对应的topic
            String topic = EventUpBuilder.getTopic(nodeId);

            // 发送注册请求
            this.mqttService.getClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));
        }
    }
}
