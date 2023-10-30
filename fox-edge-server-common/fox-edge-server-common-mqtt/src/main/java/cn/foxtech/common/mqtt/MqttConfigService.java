package cn.foxtech.common.mqtt;

import cn.foxtech.common.utils.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttConfigService {
    /**
     * MQTT订阅位置
     */
    private final String subscribe = "/fox/proxy/c2e";


    private String host = "127.0.0.1";

    private Integer port = 1883;

    private String userName = "mica";

    private String password = "123456";

    private String name = "Mica-Mqtt-Client";

    private String version = "mqtt_3_1_1";

    private String clientId = "FOX_CLIENT_CHANNEL_MQTT_CLIENT";

    private Integer keepAliveSecs = 60;

    private Integer reInterval = 5000;

    public void initialize(Map<String, Object> configs) {
        // 从redis中装载配置：如果redis没有，则默认采用application.yml的配置数据
        this.host = Maps.getOrDefault(configs, String.class, "host", this.host);
        this.port = Maps.getOrDefault(configs, Integer.class, "port", this.port);
        this.clientId = Maps.getOrDefault(configs, String.class, "clientId", this.clientId);
        this.userName = Maps.getOrDefault(configs, String.class, "user-name", this.userName);
        this.password = Maps.getOrDefault(configs, String.class, "password", this.password);
        this.name = Maps.getOrDefault(configs, String.class, "name", this.name);
        this.version = Maps.getOrDefault(configs, String.class, "version", this.version);
        this.keepAliveSecs = Maps.getOrDefault(configs, Integer.class, "keep-alive-secs", this.keepAliveSecs);
        this.reInterval = Maps.getOrDefault(configs, Integer.class, "re-interval", this.reInterval);
    }
}
