package cn.foxtech.proxy.cloud.forwarder.service;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRequestVO;
import cn.foxtech.proxy.cloud.forwarder.vo.RestfulLikeRespondVO;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * MQTT消息监听者
 */
@Component
public class MqttMessageListener implements IMqttClientMessageListener {
    @Autowired
    private MqttMessageMapping mqttMessageQueue;

    @Override
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
