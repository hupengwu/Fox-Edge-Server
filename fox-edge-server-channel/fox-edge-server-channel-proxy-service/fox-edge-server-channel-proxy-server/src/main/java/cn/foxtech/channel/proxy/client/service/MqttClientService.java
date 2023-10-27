package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

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
    @Autowired
    private ConfigManageService configManageService;
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
    private RedisTopicPuberService redisTopicPuberService;


    public boolean Initialize() {
        Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");

        // 初始化配置
        this.configService.initialize(configs);

        String subTopic = this.configService.getSubscribe();
        String pubTopic = this.configService.getPublish();
        String clientId = this.configService.getClientId() + ":" + UUID.randomUUID().toString().replace("-", "");

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + subTopic);
        logger.info("mqtt topic publish:  " + pubTopic);

        // 从把配置参数填入组件当中
        this.creator.ip(this.configService.getIp());
        this.creator.port(this.configService.getPort());
        this.creator.name(this.configService.getName());
        this.creator.username(this.configService.getUserName());
        this.creator.password(this.configService.getPassword());
        this.creator.keepAliveSecs(this.configService.getKeepAliveSecs());
        this.creator.reInterval(this.configService.getReInterval());
        this.creator.clientId(clientId);

        // 连接broker服务器
        this.mqttClient = this.creator.connect();

        // 订阅主题
        this.mqttClient.subQos0(subTopic, (context, topic, message, payload) -> {
            this.onMessage(context, topic, message, payload);
        });

        return true;
    }

    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);

        try {
            String channelType = this.getChannelType(topic);
            String rspTopic = this.getPubTopic(channelType);


            ChannelRequestVO requestVO = JsonUtils.buildObject(messageTxt, ChannelRequestVO.class);
            if (requestVO == null) {
                return;
            }

            if (requestVO.getType() == null || requestVO.getType().isEmpty()) {
                return;
            }

            // 查询响应数据
            ChannelRespondVO respondVO = redisTopicPuberService.execute(requestVO);
            if (rspTopic == null) {
                return;
            }

            String rspContext = JsonUtils.buildJson(respondVO);
            this.mqttClient.publish(rspTopic, rspContext.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }

    private String getChannelType(String topic) {
        String subTopicHead = "";
        if (this.configService.getSubscribe().endsWith("/#")) {
            subTopicHead = this.configService.getSubscribe().substring(0, this.configService.getSubscribe().length() - 2);
        } else {
            throw new ServiceException("订阅topic必须以/#结尾");
        }

        return topic.substring(subTopicHead.length() + 1);
    }

    private String getPubTopic(String channelType) {
        String pubTopicHead = "";
        if (this.configService.getPublish().endsWith("/#")) {
            pubTopicHead = this.configService.getPublish().substring(0, this.configService.getPublish().length() - 2);
        } else {
            throw new ServiceException("发送topic必须以/#结尾");
        }

        return pubTopicHead + "/" + channelType;
    }
}
