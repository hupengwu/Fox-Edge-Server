/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import lombok.AccessLevel;
import lombok.Getter;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * MqttClient实体，代表一个独立的MQTT连接
 */
@Getter(value = AccessLevel.PUBLIC)
public class MqttClientEntity {
    private static final Logger logger = LoggerFactory.getLogger(MqttCompService.class);
    /**
     * MQTT的创建者
     */
    private final MqttClientCreator creator = MqttClient.create();

    /**
     * 配置服务：从redis中获得配置信息
     */
    private MqttClientConfig config = new MqttClientConfig();
    /**
     * 客户端连接
     */
    private MqttClient client;


    private MqttClientListener listener;

    public void create(MqttClientConfig config, MqttClientListener listener) {
        // 初始化配置
        this.config = config;
        this.listener = listener;


        String clientId = this.config.getClientId();
        String subTopic = this.listener.getClientHandler().getTopic();

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + subTopic);

        MqttVersion mqttVersion = MqttVersion.MQTT_3_1_1;
        if (this.config.getVersion().equalsIgnoreCase("MQTT_3_1")) {
            mqttVersion = MqttVersion.MQTT_3_1;
        }
        if (this.config.getVersion().equalsIgnoreCase("MQTT_3_1_1")) {
            mqttVersion = MqttVersion.MQTT_3_1_1;
        }
        if (this.config.getVersion().equalsIgnoreCase("MQTT_5")) {
            mqttVersion = MqttVersion.MQTT_5;
        }

        // 从把配置参数填入组件当中
        this.creator.version(mqttVersion);
        this.creator.ip(this.config.getHost());
        this.creator.port(this.config.getPort());
        this.creator.name(this.config.getName());
        this.creator.username(this.config.getUserName());
        this.creator.password(this.config.getPassword());
        this.creator.keepAliveSecs(this.config.getKeepAliveSecs());
        this.creator.reInterval(this.config.getReInterval());
        this.creator.clientId(clientId);
        this.creator.reconnect(true);

        // 连接broker服务器
        this.client = this.creator.connect();

        // 如果填写了，那么就订阅
        if (subTopic != null && !subTopic.isEmpty()) {
            // 订阅主题
            this.client.subQos0(subTopic, this.listener);
        }
    }

    public MqttClient subscribe(String subTopic, MqttQoS mqttQoS) {
        return this.client.subscribe(subTopic, mqttQoS, this.listener);
    }
}
