package cn.foxtech.device.simulator.mqtt.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ApplicationConfig {
    /**
     * MQTT订阅位置
     */
    @Value("${mqtt.client.topic.subscribe}")
    private String subscribe;

    /**
     * MQTT发布位置
     */
    @Value("${mqtt.client.topic.publish}")
    private String publish;


    /**
     * MQTT Server的位置
     */
    @Value("${mqtt.client.ip}")
    private String ip = "127.0.0.1";

    /**
     * 客户端的ID
     */
    private String clientId = "MQTT_FOX_CLIENT";
}
