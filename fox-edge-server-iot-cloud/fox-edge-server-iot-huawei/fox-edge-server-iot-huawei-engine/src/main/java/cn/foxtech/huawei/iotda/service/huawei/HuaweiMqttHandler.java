package cn.foxtech.huawei.iotda.service.huawei;

import cn.foxtech.common.mqtt.MqttClientHandler;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

public class HuaweiMqttHandler extends MqttClientHandler {
    @Override
    public String getTopic() {
        return "#";
    }

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);
    }
}