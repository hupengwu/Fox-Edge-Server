package cn.foxtech.proxy.cloud.forwarder.service;

import cn.foxtech.proxy.cloud.forwarder.config.ApplicationConfig;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import net.dreamlu.iot.mqtt.spring.client.MqttClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author wsq
 */
@Component
public class MqttClientService {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientService.class);
    @Autowired
    private ApplicationConfig constant;

    @Autowired
    private MqttClientTemplate mqttClientTemplate;

    @Autowired
    private MqttMessageListener mqttMessageListener;

    @Autowired
    private MqttMessageRespond mqttMessageRespond;

    @Autowired
    private MqttClientCreator mqttClientCreator;


    public boolean Initialize() {
        String subTopic = this.constant.getSubscribe();
        String pubTopic = this.constant.getPublish();
        String clientId = this.constant.getClientId() + ":" + UUID.randomUUID().toString().replace("-", "");

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + subTopic);
        logger.info("mqtt topic publish:  " + pubTopic);

        this.mqttClientCreator.clientId(clientId);

        // 订阅
        this.mqttClientTemplate.subQos0(subTopic, this.mqttMessageListener);

        // 启动消息处理
        this.mqttMessageRespond.schedule();
        return true;
    }


}
