package cn.foxtech.iot.whzktl.service.service;

import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.iot.whzktl.service.notify.DeviceValueTypeNotify;
import cn.foxtech.iot.whzktl.service.remote.MqttHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WhZktlIotService {
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
    @Getter
    private String extendField = "";
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DeviceValueTypeNotify deviceValueTypeNotify;

    public void initialize() {
        // 取出全局配置参数
        Map<String, Object> whzktl = (Map<String, Object>) this.localConfigService.getConfigs().getOrDefault("whzktl", new HashMap<>());

        String publish = (String) whzktl.getOrDefault("publish", "");
        String subscribe = (String) whzktl.getOrDefault("subscribe", "");
        this.publish = publish.replace("{edgeId}", OSInfoUtils.getCPUID());
        this.subscribe = subscribe.replace("{edgeId}", OSInfoUtils.getCPUID());


        Map<String, Object> extend = (Map<String, Object>) this.localConfigService.getConfigs().getOrDefault("extend", new HashMap<>());
        this.extendField = (String) extend.getOrDefault("extendField", "huaweiIotDA");

        // 绑定一个类型级别的数据变更通知
        ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(DeviceValueEntity.class);
        consumerRedisService.bind(this.deviceValueTypeNotify);

        // 初始化MQTT组件
        MqttHandler mqttHandler = new MqttHandler();
        mqttHandler.setTopic(this.subscribe);
        this.remoteMqttService.initialize(mqttHandler);
    }
}
