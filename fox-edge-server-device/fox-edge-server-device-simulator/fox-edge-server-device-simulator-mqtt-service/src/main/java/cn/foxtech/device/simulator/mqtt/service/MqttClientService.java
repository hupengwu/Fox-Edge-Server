package cn.foxtech.device.simulator.mqtt.service;

import cn.foxtech.device.simulator.mqtt.config.ApplicationConfig;
import cn.foxtech.common.utils.hex.HexUtils;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import net.dreamlu.iot.mqtt.spring.client.MqttClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * @author wsq
 */
@Component
public class MqttClientService {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientService.class);
    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private MqttClientTemplate mqttClientTemplate;

    @Autowired
    private MqttClientCreator mqttClientCreator;

    @Autowired
    private MqttSimulatorService simulatorService;


    public boolean Initialize() {
        String subTopic = this.applicationConfig.getSubscribe();
        String pubTopic = this.applicationConfig.getPublish();
        String clientId = this.applicationConfig.getClientId() + ":" + UUID.randomUUID().toString().replace("-", "");

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + subTopic);
        logger.info("mqtt topic publish:  " + pubTopic);

        this.mqttClientCreator.clientId(clientId);

        // 订阅
        this.mqttClientTemplate.subQos0(subTopic, (context, topic, message, payload) -> {
            this.onMessage(context, topic, message, payload);
        });

        return true;
    }

    public void onMessage(ChannelContext context, String reqTopic, MqttPublishMessage message, ByteBuffer payload) {
        String tailId = this.geTailId(reqTopic);
        String rspTopic = this.getPubTopic(tailId);


        // 原文是二进制的格式，需要先转成十六进制文本
        String reqContext = HexUtils.byteArrayToHexString(payload.array());

        // 查询响应数据
        String rspContext = simulatorService.execute("", reqContext, 2000);
        if (rspTopic == null) {
            return;
        }
        if (rspContext == null) {
            return;
        }


        // 原文是二进制的格式，需要将十六进制文本转换为二进制格式返回去
        byte[] rspArray = HexUtils.hexStringToByteArray(rspContext);
        this.mqttClientTemplate.publish(rspTopic, rspArray);
    }

    private String geTailId(String topic) {
        String subTopicHead = "";
        if (this.applicationConfig.getSubscribe().endsWith("/#")) {
            subTopicHead = this.applicationConfig.getSubscribe().substring(0, this.applicationConfig.getSubscribe().length() - 2);
        } else {
            throw new RuntimeException("订阅topic必须以/#结尾");
        }

        return topic.substring(subTopicHead.length() + 1);
    }

    private String getPubTopic(String tailId) {
        String pubTopicHead = "";
        if (this.applicationConfig.getPublish().endsWith("/#")) {
            pubTopicHead = this.applicationConfig.getPublish().substring(0, this.applicationConfig.getPublish().length() - 2);
        } else {
            throw new RuntimeException("发送topic必须以/#结尾");
        }

        return pubTopicHead + "/" + tailId;
    }
}
