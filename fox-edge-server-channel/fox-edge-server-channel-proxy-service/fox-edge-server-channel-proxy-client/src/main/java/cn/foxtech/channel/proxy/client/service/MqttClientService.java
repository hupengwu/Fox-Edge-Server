package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.ConfigManageService;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
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
    /**
     * 配置服务：从redis中获得配置信息
     */
    @Autowired
    private MqttConfigService configService;
    /**
     * 客户端连接
     */
    private MqttClient mqttClient;
    /**
     * channel的通用配置信息
     */
    @Autowired
    private ChannelProperties constants;

    @Autowired
    private ConfigManageService configManageService;

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
            ChannelRespondVO respondVO = JsonUtils.buildObject(messageTxt, ChannelRespondVO.class);

            // 根据UUID是否为空，判定主从问答，还是主动上报回来的报文
            if (respondVO.getUuid() != null && !respondVO.getUuid().isEmpty()) {
                // 带UUID报文：这是Exchange需要的的主从应答报文
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
            } else {
                // 不带UUID报：这是Subscribe需要的主动上报报文
                SyncQueueObjectMap.inst().push(this.constants.getChannelType(), respondVO, 100);
            }


        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }


}
