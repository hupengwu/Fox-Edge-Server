package cn.foxtech.common.mqtt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;

@Component
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttClientListener implements IMqttClientMessageListener {
    private MqttClientHandler clientHandler = new MqttClientHandler();

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        this.clientHandler.onMessage(context, topic, message, payload);
    }
}
