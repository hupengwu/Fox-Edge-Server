package cn.foxtech.channel.proxy.client.service;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttConfigService {
    @Autowired
    private ConfigManageService configManageService;
    /**
     * MQTT订阅位置
     */
    @Value("${mqtt.client.topic.subscribe}")
    private String subscribe = "/foxteam/v1/proxy/response";

    /**
     * MQTT发布位置
     */
    @Value("${mqtt.client.topic.publish}")
    private String publish = "/foxteam/v1/proxy/request";


    @Value("${mqtt.client.ip}")
    private String ip = "127.0.0.1";

    @Value("${mqtt.client.port}")
    private Integer port = 1883;

    @Value("${mqtt.client.user-name}")
    private String userName = "mica";

    @Value("${mqtt.client.password}")
    private String password = "123456";

    @Value("${mqtt.client.name}")
    private String name = "Mica-Mqtt-Client";

    @Value("${mqtt.client.version}")
    private String version = "mqtt_3_1_1";

    @Value("${mqtt.client.clientId}")
    private String clientId = "FOX_CLIENT_CHANNEL_MQTT_CLIENT";

    @Value("${mqtt.client.keep-alive-secs}")
    private Integer keepAliveSecs = 60;

    @Value("${mqtt.client.re-interval}")
    private Integer reInterval = 5000;

    public void initialize() {
        // 从redis中装载配置：如果redis没有，则默认采用application.yml的配置数据
        this.ip = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "ip", this.ip);
        this.port = this.configManageService.getOrDefaultValue("mqttConfig", Integer.class, "mqtt", "client", "port", this.port);
        this.clientId = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "clientId", this.clientId);
        this.userName = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "user-name", this.userName);
        this.password = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "password", this.password);
        this.name = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "name", this.name);
        this.version = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "version", this.version);
        this.keepAliveSecs = this.configManageService.getOrDefaultValue("mqttConfig", Integer.class, "mqtt", "client", "keep-alive-secs", this.keepAliveSecs);
        this.reInterval = this.configManageService.getOrDefaultValue("mqttConfig", Integer.class, "mqtt", "client", "re-interval", this.reInterval);

        this.subscribe = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "topic", "subscribe", this.subscribe);
        this.publish = this.configManageService.getOrDefaultValue("mqttConfig", String.class, "mqtt", "client", "topic", "publish", this.publish);
    }
}
