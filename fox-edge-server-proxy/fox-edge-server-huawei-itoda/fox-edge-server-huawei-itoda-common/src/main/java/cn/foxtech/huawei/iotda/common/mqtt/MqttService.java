package cn.foxtech.thingsboard.common.mqtt;

import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.mqtt.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MqttService {
    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private ConfigManageService configManageService;

    public void initialize() {
        // 绑定当前的handler
        this.mqttClientService.getMqttClientListener().setClientHandler(new MqttHandler());

        // 初始化连接
        Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");
        this.mqttClientService.Initialize(configs);
    }
}
