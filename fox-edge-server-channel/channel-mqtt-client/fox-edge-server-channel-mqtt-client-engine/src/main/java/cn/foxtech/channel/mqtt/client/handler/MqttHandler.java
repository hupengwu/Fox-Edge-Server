package cn.foxtech.channel.mqtt.client.handler;

import cn.foxtech.channel.mqtt.client.service.ReportService;
import cn.foxtech.common.mqtt.MqttClientHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MqttHandler extends MqttClientHandler {
    private static final Logger logger = LoggerFactory.getLogger(MqttHandler.class);

    private String topic = "#";

    @Setter
    private ReportService reportService;

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        try {
            String messageTxt = ByteBufferUtil.toString(payload);

            // 保存PDU到接收缓存
            this.reportService.push(topic, messageTxt);
        } catch (Throwable e) {
            logger.warn(e.toString());
        }
    }
}