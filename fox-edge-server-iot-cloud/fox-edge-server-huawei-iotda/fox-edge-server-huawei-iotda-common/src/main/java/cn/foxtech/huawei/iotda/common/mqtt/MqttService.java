package cn.foxtech.huawei.iotda.common.mqtt;

import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.mqtt.MqttClientService;
import lombok.Getter;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MqttService {
    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private ConfigManageService configManageService;

    @Getter
    private String productId = "";

    @Getter
    private String nodeId = "";

    @Getter
    private String deviceId = "";

    @Getter
    private Integer deviceOnlinePush = 30;

    public void initialize() {
        // 绑定当前的handler
        this.mqttClientService.getMqttClientListener().setClientHandler(new MqttHandler());

        // 初始化连接
        Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");
        this.mqttClientService.Initialize(configs);

        this.productId = (String) configs.getOrDefault("productId", "");
        this.nodeId = (String) configs.getOrDefault("nodeId", "");
        this.deviceId = (String) configs.getOrDefault("deviceId", "");
        this.deviceOnlinePush = (Integer) configs.getOrDefault("deviceOnlinePush", 30);
    }

    public MqttClient getClient() {
        return this.mqttClientService.getMqttClient();
    }
}
