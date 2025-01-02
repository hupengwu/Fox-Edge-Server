/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 消息响应Handler，可以通过派生该类型，并且绑定在Listener上，实现自定义的消息处理
 */
public class MqttClientHandler {
    public String getTopic() {
        return "#";
    }

    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, byte[] payload) {
        String messageTxt = new String(payload, StandardCharsets.UTF_8);
    }
}
