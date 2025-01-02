/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import cn.foxtech.common.utils.MapUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

/**
 * MQTT的通信参数配置对象
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttClientConfig {
    /**
     * MQTT订阅位置
     */
    private String subscribe = "/fox/proxy/c2e";


    private String host = "127.0.0.1";

    private Integer port = 1883;

    private String userName = "mica";

    private String password = "123456";

    private String name = "Mica-Mqtt-Client";

    private String version = "mqtt_3_1_1";

    private String clientId = "FOX_CLIENT_CHANNEL_MQTT_CLIENT";

    private Integer keepAliveSecs = 60;

    private Integer reInterval = 5000;

    public void instance(Map<String, Object> configs) {
        // 从redis中装载配置：如果redis没有，则默认采用application.yml的配置数据
        this.host = MapUtils.getOrDefault(configs, String.class, "host", this.host);
        this.port = MapUtils.getOrDefault(configs, Integer.class, "port", this.port);
        this.clientId = MapUtils.getOrDefault(configs, String.class, "clientId", this.clientId);
        this.userName = MapUtils.getOrDefault(configs, String.class, "user-name", this.userName);
        this.password = MapUtils.getOrDefault(configs, String.class, "password", this.password);
        this.name = MapUtils.getOrDefault(configs, String.class, "name", this.name);
        this.version = MapUtils.getOrDefault(configs, String.class, "version", this.version);
        this.keepAliveSecs = MapUtils.getOrDefault(configs, Integer.class, "keep-alive-secs", this.keepAliveSecs);
        this.reInterval = MapUtils.getOrDefault(configs, Integer.class, "re-interval", this.reInterval);

        if (this.clientId == null || this.clientId.isEmpty()) {
            this.clientId = "clientId:" + UUID.randomUUID();
        }
    }

    public MqttClientConfig clone() {
        MqttClientConfig clone = new MqttClientConfig();
        clone.subscribe = this.subscribe;
        clone.host = this.host;
        clone.port = this.port;
        clone.userName = this.userName;
        clone.password = this.password;
        clone.name = this.name;
        clone.version = this.version;
        clone.clientId = this.clientId;
        clone.keepAliveSecs = this.keepAliveSecs;
        clone.reInterval = this.reInterval;

        return clone;
    }
}
