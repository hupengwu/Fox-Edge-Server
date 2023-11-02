package cn.foxtech.huawei.iotda.service.huawei;

import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HuaweiIoTDAService {
    /**
     * 从华为物联网平台，导出来的物模型文件
     */
    @Getter
    private final Map<String, Map<String, Object>> modelMap = new HashMap<>();

    @Autowired
    private RedisConsoleService consoleService;
    @Getter
    private String productId = "";
    @Getter
    private String nodeId = "";
    @Getter
    private String deviceId = "";

    @Getter
    private String provider = "";

    @Getter
    private String extendField = "";

    @Getter
    private Integer deviceOnlinePush = 30;
    @Autowired
    private RemoteMqttService remoteMqttService;
    @Autowired
    private LocalConfigService localConfigService;

    public void initialize() {
        // 初始化MQTT组件
        this.remoteMqttService.initialize(new HuaweiMqttHandler());

        // 取出全局配置参数
        Map<String, Object> huaweiIoTDA = (Map<String, Object>) this.localConfigService.getConfigs().getOrDefault("huaweiIoTDA", new HashMap<>());

        this.productId = (String) huaweiIoTDA.getOrDefault("productId", "");
        this.nodeId = (String) huaweiIoTDA.getOrDefault("nodeId", "");
        this.deviceId = (String) huaweiIoTDA.getOrDefault("deviceId", "");
        this.provider = (String) huaweiIoTDA.getOrDefault("provider", "HuaWei-IoTDA");
        this.deviceOnlinePush = (Integer) huaweiIoTDA.getOrDefault("deviceOnlinePush", 30);

        Map<String, Object> extend = (Map<String, Object>) this.localConfigService.getConfigs().getOrDefault("extend", new HashMap<>());
        this.extendField = (String) extend.getOrDefault("extendField", "huaweiIotDA");

    }
}
