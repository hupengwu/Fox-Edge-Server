/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import lombok.AccessLevel;
import lombok.Getter;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wsq
 */
@Component
@Getter(value = AccessLevel.PUBLIC)
public class MqttClientService {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientService.class);
    /**
     * MQTT的创建者
     */
    private final MqttClientCreator creator = MqttClient.create();

    /**
     * 配置服务：从redis中获得配置信息
     */
    @Autowired
    private MqttConfigService configService;
    /**
     * 客户端连接
     */
    private MqttClient mqttClient;


    @Autowired
    private MqttClientListener mqttClientListener;

    public boolean Initialize(Map<String, Object> configs) {
        // 初始化配置
        this.configService.initialize(configs);


        String clientId = this.configService.getClientId();
        String subTopic = this.mqttClientListener.getClientHandler().getTopic();

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + subTopic);

        // 从把配置参数填入组件当中
        this.creator.ip(this.configService.getHost());
        this.creator.port(this.configService.getPort());
        this.creator.name(this.configService.getName());
        this.creator.username(this.configService.getUserName());
        this.creator.password(this.configService.getPassword());
        this.creator.keepAliveSecs(this.configService.getKeepAliveSecs());
        this.creator.reInterval(this.configService.getReInterval());
        this.creator.clientId(clientId);

        // 连接broker服务器
        this.mqttClient = this.creator.connect();

        // 如果填写了，那么就订阅
        if (subTopic != null && !subTopic.isEmpty()) {
            // 订阅主题
            this.mqttClient.subQos0(subTopic, this.mqttClientListener);
        }

        return true;
    }


}
