package cn.foxtech.iot.whzktl.service.whzktl;

import cn.foxtech.common.mqtt.MqttClientHandler;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

public class WhZktlMqttHandler extends MqttClientHandler {
    @Setter
    @Getter
    private String topic;

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);
    }
}