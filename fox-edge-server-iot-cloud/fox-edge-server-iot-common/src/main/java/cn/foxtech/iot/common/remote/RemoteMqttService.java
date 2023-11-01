package cn.foxtech.iot.common.remote;

import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.mqtt.MqttClientService;
import lombok.Setter;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RemoteMqttService {
    @Autowired
    private MqttClientService mqttClientService;

    @Setter
    private Map<String, Object> mqttConfig = new HashMap<>();


    public void initialize(MqttClientHandler clientHandler) {
        // 绑定当前的handler
        this.mqttClientService.getMqttClientListener().setClientHandler(clientHandler);
        this.mqttClientService.Initialize(this.mqttConfig);
    }

    public MqttClient getClient() {
        return this.mqttClientService.getMqttClient();
    }
}
