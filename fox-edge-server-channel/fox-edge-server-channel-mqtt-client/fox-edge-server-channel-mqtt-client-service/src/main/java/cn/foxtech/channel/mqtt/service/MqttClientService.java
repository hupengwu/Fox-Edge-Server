package cn.foxtech.channel.mqtt.service;

import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
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
     * 配置服务：从redis中获得配置信息
     */
    @Autowired
    private MqttConfigService configService;
    /**
     * 客户端连接
     */
    private MqttClient clientService;
    /**
     * channel的通用配置信息
     */
    @Autowired
    private ChannelProperties constants;

    public boolean Initialize() {
        // 初始化配置
        this.configService.initialize();


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
        this.clientService = this.creator.connect();

        // 订阅主题
        this.clientService.subQos0(subTopic, (context, topic, message, payload) -> {
            this.onMessage(context, topic, message, payload);
        });

        return true;
    }

    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        try {
            String tailId = this.geTailId(topic);
            String reqTopic = this.getPubTopic(tailId);

            String content = HexUtils.byteArrayToHexString(payload.array());

            // 判定是主从问答返回的数据，还是设备主动上报的数据
            if (SyncFlagObjectMap.inst().containsKey(reqTopic)) {
                // 解决openjdk不带pair包的问题
                Map<Integer, byte[]> pair = new HashMap<>();
                pair.put(0, payload.array());
                SyncFlagObjectMap.inst().notifyConstant(reqTopic, pair);
            } else {
                // 执行请求
                ChannelRespondVO respondVO = new ChannelRespondVO();

                respondVO.setUuid(null);
                respondVO.setType(constants.getChannelType());
                respondVO.setName(reqTopic);
                respondVO.setRecv(content);

                // 填充到缓存队列
                SyncQueueObjectMap.inst().push(constants.getChannelType(), respondVO, 1000);
            }

        } catch (Throwable e) {
            logger.warn(e.toString());
        }
    }


    private String geTailId(String topic) {
        String subTopicHead = "";
        if (this.configService.getSubscribe().endsWith("/#")) {
            subTopicHead = this.configService.getSubscribe().substring(0, this.configService.getSubscribe().length() - 2);
        } else {
            throw new ServiceException("订阅topic必须以/#结尾");
        }

        return topic.substring(subTopicHead.length() + 1);
    }

    private String getPubTopic(String tailId) {
        String pubTopicHead = "";
        if (this.configService.getPublish().endsWith("/#")) {
            pubTopicHead = this.configService.getPublish().substring(0, this.configService.getPublish().length() - 2);
        } else {
            throw new ServiceException("发送topic必须以/#结尾");
        }

        return pubTopicHead + "/" + tailId;
    }


}
