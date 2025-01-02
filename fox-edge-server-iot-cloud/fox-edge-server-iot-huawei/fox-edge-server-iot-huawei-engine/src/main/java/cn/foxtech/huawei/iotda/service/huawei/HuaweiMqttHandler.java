/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.huawei.iotda.service.huawei;

import cn.foxtech.common.mqtt.MqttClientHandler;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HuaweiMqttHandler extends MqttClientHandler {
    @Override
    public String getTopic() {
        return "#";
    }

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, byte[] payload) {
        String messageTxt = new String(payload, StandardCharsets.UTF_8);
    }
}