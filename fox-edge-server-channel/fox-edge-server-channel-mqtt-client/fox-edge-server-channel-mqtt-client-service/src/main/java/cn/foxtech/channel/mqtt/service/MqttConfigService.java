package cn.foxtech.channel.mqtt.service;


import cn.foxtech.channel.common.service.ConfigManageService;
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

//    enabled: true               # 是否开启客户端，默认：true
//    ip: 39.108.137.38           # 连接的服务端 ip ，默认：127.0.0.1
//    port: 1883                  # 端口：默认：1883
//    name: Mica-Mqtt-Client      # 名称，默认：Mica-Mqtt-Client
//    clientId: 000001            # 客户端Id（非常重要，一般为设备 sn，不可重复）
//    user-name: mica             # 认证的用户名
//    password: 123456            # 认证的密码
//    timeout: 5                  # 超时时间，单位：秒，默认：5秒
//    reconnect: true             # 是否重连，默认：true
//    re-interval: 5000           # 重连时间，默认 5000 毫秒
//    version: mqtt_3_1_1         # mqtt 协议版本，可选 MQTT_3_1、mqtt_3_1_1、mqtt_5，默认：mqtt_3_1_1
//    read-buffer-size: 8KB       # 接收数据的 buffer size，默认：8k
//    max-bytes-in-message: 10MB  # 消息解析最大 bytes 长度，默认：10M
//    buffer-allocator: heap      # 堆内存和堆外内存，默认：堆内存
//    keep-alive-secs: 60         # keep-alive 时间，单位：秒
//    clean-session: true         # mqtt clean session，默认：true
//    use-ssl: false              # 是否启用 ssl，默认：false


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
