package cn.foxtech.kernel.system.service.restfullike.mqtt;

import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.mqtt.MqttClientService;
import cn.foxtech.kernel.common.service.EdgeService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MqttRestfulLikeService {
    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private InitialConfigService configService;

    @Setter
    private Map<String, Object> mqttConfig = new HashMap<>();

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private MqttRestfulLikeController controller;


    public void initialize() {
        // 读取配置参数
        this.configService.initialize("mqttConfig", "mqttConfig.json");
        Map<String, Object> configValue = this.configService.getConfigParam("mqttConfig");
        this.mqttConfig = (Map<String, Object>) configValue.getOrDefault("mqtt", new HashMap<>());
        Boolean enable = (Boolean) configValue.getOrDefault("enable", false);

        // 检查：是否需要开启MQTT订阅
        if (!Boolean.TRUE.equals(enable)) {
            return;
        }

        // 提取参数
        Map<String, Object> topic = (Map<String, Object>) configValue.getOrDefault("topic", new HashMap<>());
        String request = (String) topic.getOrDefault("request", "#");
        String respond = (String) topic.getOrDefault("respond", "");

        // 生成handler
        MqttRestfulLikeHandler handler = new MqttRestfulLikeHandler();
        request = request.replaceAll("\\{edgeId\\}", edgeService.getCPUID());
        respond = respond.replaceAll("\\{edgeId\\}", edgeService.getCPUID());
        handler.setRequest(request);
        handler.setRespond(respond);
        handler.setController(this.controller);

        // 初始化MQTT
        this.mqttClientService.getMqttClientListener().setClientHandler(handler);
        this.mqttClientService.Initialize(this.mqttConfig);
    }
}
