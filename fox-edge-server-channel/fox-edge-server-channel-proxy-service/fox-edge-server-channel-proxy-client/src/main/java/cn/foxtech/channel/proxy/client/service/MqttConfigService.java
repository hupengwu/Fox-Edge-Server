package cn.foxtech.channel.proxy.client.service;


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
    private String subscribe = "/foxteam/v1/proxy/response";

    /**
     * MQTT发布位置
     */
    private String publish = "/foxteam/v1/proxy/request";


    private String ip = "127.0.0.1";

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
        this.ip = Maps.getOrDefault(configs, String.class, "mqtt", "client", "ip", this.ip);
        this.port = Maps.getOrDefault(configs, Integer.class, "mqtt", "client", "port", this.port);
        this.clientId = Maps.getOrDefault(configs, String.class, "mqtt", "client", "clientId", this.clientId);
        this.userName = Maps.getOrDefault(configs, String.class, "mqtt", "client", "user-name", this.userName);
        this.password = Maps.getOrDefault(configs, String.class, "mqtt", "client", "password", this.password);
        this.name = Maps.getOrDefault(configs, String.class, "mqtt", "client", "name", this.name);
        this.version = Maps.getOrDefault(configs, String.class, "mqtt", "client", "version", this.version);
        this.keepAliveSecs = Maps.getOrDefault(configs, Integer.class, "mqtt", "client", "keep-alive-secs", this.keepAliveSecs);
        this.reInterval = Maps.getOrDefault(configs, Integer.class, "mqtt", "client", "re-interval", this.reInterval);

        this.subscribe = Maps.getOrDefault(configs, String.class, "mqtt", "client", "topic", "subscribe", this.subscribe);
        this.publish = Maps.getOrDefault(configs, String.class, "mqtt", "client", "topic", "publish", this.publish);
    }
}
