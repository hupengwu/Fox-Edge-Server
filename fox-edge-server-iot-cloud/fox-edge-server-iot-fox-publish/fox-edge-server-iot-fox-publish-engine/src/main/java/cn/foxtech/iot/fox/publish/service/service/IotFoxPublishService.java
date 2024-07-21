package cn.foxtech.iot.fox.publish.service.service;

import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.fox.publish.service.remote.MqttHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IotFoxPublishService {
    /**
     * 从华为物联网平台，导出来的物模型文件
     */
    @Getter
    private final Map<String, Map<String, Object>> modelMap = new HashMap<>();
    @Getter
    private final Integer deviceOnlinePush = 30;
    @Getter
    private String publish = "";
    @Getter
    private String subscribe = "";

    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private LocalConfigService localConfigService;


    public void initialize() {
        // 取出全局配置参数
        Map<String, Object> topic = (Map<String, Object>) this.localConfigService.getConfig().getOrDefault("topic", new HashMap<>());

        String publish = (String) topic.getOrDefault("publish", "");
        String subscribe = (String) topic.getOrDefault("subscribe", "");
        this.publish = publish.replace("{edgeId}", OSInfoUtils.getCPUID());
        this.subscribe = subscribe.replace("{edgeId}", OSInfoUtils.getCPUID());

        // 初始化MQTT组件
        MqttHandler mqttHandler = new MqttHandler();
        mqttHandler.setTopic(this.subscribe);
        this.remoteMqttService.initialize(mqttHandler);
    }
}
