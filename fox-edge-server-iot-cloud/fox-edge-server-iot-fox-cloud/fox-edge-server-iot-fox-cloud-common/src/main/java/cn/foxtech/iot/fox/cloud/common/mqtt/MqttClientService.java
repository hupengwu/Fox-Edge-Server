/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.iot.fox.cloud.common.mqtt;

import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.osinfo.OSInfoUtils;
import cn.foxtech.core.exception.ServiceException;
import lombok.AccessLevel;
import lombok.Getter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
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
    private MqttMessageC2E mqttMessageQueue;

    /**
     * 配置服务：从redis中获得配置信息
     */
    private MqttConfigService mqttConfigService = new MqttConfigService();
    /**
     * 客户端连接
     */
    private MqttClient mqttClient;

    @Autowired
    private InitialConfigService configService;

    private String subTopic;

    private String subscribeAggregate;
    private String publish2aggregate;

    private String subscribeForward;
    private String publish2forward;


    public boolean Initialize() {
        this.configService.initialize("serverConfig", "serverConfig.json");
        Map<String, Object> configs = this.configService.getConfigParam("serverConfig");

        Map<String, Object> remoteConfig = (Map<String, Object>) configs.getOrDefault("remote", new HashMap<>());
        String mode = (String) remoteConfig.getOrDefault("mode", "http");

        // 检测：是否为MQTT连接方式
        if (!"mqtt".equals(mode)) {
            return false;
        }

        Map<String, Object> mqttConfig = (Map<String, Object>) remoteConfig.getOrDefault("mqtt", new HashMap<>());

        // 初始化配置
        this.mqttConfigService.instance(mqttConfig);


        String clientId = this.mqttConfigService.getClientId() + ":" + UUID.randomUUID().toString().replace("-", "");
        this.subTopic = this.mqttConfigService.getSubscribe() + "/" + this.edgeId + "/#";
        this.publish2aggregate = this.mqttConfigService.getPublish2Aggregate() + "/" + this.edgeId;
        this.publish2forward = this.mqttConfigService.getPublish2Forward() + "/" + this.edgeId;
        this.subscribeAggregate = this.mqttConfigService.getSubscribe() + "/" + this.edgeId + "/aggregate";
        this.subscribeForward = this.mqttConfigService.getSubscribe() + "/" + this.edgeId + "/forward";

        logger.info("mqtt clientId       :" + clientId);
        logger.info("mqtt topic subscribe:" + this.subTopic);
        logger.info("mqtt topic publish2aggregate:  " + this.publish2aggregate);
        logger.info("mqtt topic publish2forward:  " + this.publish2forward);

        MqttVersion mqttVersion = MqttVersion.MQTT_3_1_1;
        if (this.mqttConfigService.getVersion().equalsIgnoreCase("MQTT_3_1")) {
            mqttVersion = MqttVersion.MQTT_3_1;
        }
        if (this.mqttConfigService.getVersion().equalsIgnoreCase("MQTT_3_1_1")) {
            mqttVersion = MqttVersion.MQTT_3_1_1;
        }
        if (this.mqttConfigService.getVersion().equalsIgnoreCase("MQTT_5")) {
            mqttVersion = MqttVersion.MQTT_5;
        }

        // 从把配置参数填入组件当中
        this.creator.version(mqttVersion);
        this.creator.ip(this.mqttConfigService.getIp());
        this.creator.port(this.mqttConfigService.getPort());
        this.creator.name(this.mqttConfigService.getName());
        this.creator.username(this.mqttConfigService.getUserName());
        this.creator.password(this.mqttConfigService.getPassword());
        this.creator.keepAliveSecs(this.mqttConfigService.getKeepAliveSecs());
        this.creator.reInterval(this.mqttConfigService.getReInterval());
        this.creator.clientId(clientId);

        // 连接broker服务器
        this.mqttClient = this.creator.connect();

        // 订阅主题
        this.mqttClient.subQos0(subTopic, (context, topic, message, payload) -> {
            this.onMessage(context, topic, message, payload);
        });

        return true;
    }

    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, byte[] payload) {
        String messageTxt = new String(payload, StandardCharsets.UTF_8);

        if (this.subscribeAggregate.equals(topic)) {
            this.receiveAggregate(messageTxt);
        }
        if (this.subscribeForward.equals(topic)) {
            this.receiveForward(messageTxt);
        }
    }

    private void receiveAggregate(String messageTxt) {
        RestfulLikeRespondVO respondVO = JsonUtils.buildObjectWithoutException(messageTxt, RestfulLikeRespondVO.class);
        if (respondVO == null) {
            return;
        }

        if (MethodUtils.hasEmpty(respondVO.getUuid())) {
            return;
        }

        MqttMessageE2C.inst().notifyConstant(respondVO.getUuid(), respondVO);
    }

    private void receiveForward(String messageTxt) {
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
