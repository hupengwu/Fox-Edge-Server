/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.mqtt;

import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

public class MqttClientHandler {
    public String getTopic() {
        return "#";
    }

    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);
    }
}
