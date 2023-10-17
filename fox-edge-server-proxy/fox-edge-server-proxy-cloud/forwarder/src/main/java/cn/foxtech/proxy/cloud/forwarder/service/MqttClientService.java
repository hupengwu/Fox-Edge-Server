package cn.foxtech.proxy.cloud.forwarder.service;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.proxy.cloud.common.service.ConfigManageService;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRespondVO;
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
import java.util.HashMap;
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
     * 设备ID：CPU序列号
     */
    private final String edgeId = OSInfoUtils.getCPUID();
    @Autowired
    private MqttMessageMapping mqttMessageQueue;

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
    private ConfigManageService configManageService;

    public boolean Initialize() {
        Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");
        Map<String, Object> cloudConfig = (Map<String, Object>)configs.getOrDefault("cloud",new HashMap<>());
        Map<String, Object> mqttConfig = (Map<String, Object>)cloudConfig.getOrDefault("mqtt",new HashMap<>());

        // 初始化配置
        this.configService.initialize(mqttConfig);


        String subTopic = this.configService.getSubscribe().replace("{devId}",this.edgeId);
        String pubTopic = this.configService.getPublish().replace("{devId}",this.edgeId);
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

        RestfulLikeRequestVO requestVO = null;
        try {
            requestVO = JsonUtils.buildObject(messageTxt, RestfulLikeRequestVO.class);
            if (requestVO.getUuid() == null || requestVO.getUuid().isEmpty()) {
                throw new ServiceException("必须包含uuid");
            }
            if (requestVO.getMethod() == null || requestVO.getMethod().isEmpty()) {
                throw new ServiceException("必须包含method");
            }
            if (requestVO.getResource() == null || requestVO.getResource().isEmpty()) {
                throw new ServiceException("必须包含resource");
            }

            // 查询响应数据
            this.mqttMessageQueue.insertRequestVO(requestVO);
        } catch (Exception e) {
            RestfulLikeRespondVO respondVO = RestfulLikeRespondVO.error(e.getMessage());
            if (requestVO != null) {
                respondVO.bindVO(requestVO);
            }

            this.mqttMessageQueue.insertRespondVO(respondVO);
        }
    }


}
