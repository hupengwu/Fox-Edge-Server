package cn.foxtech.zkturing.service.remote;

import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;
import java.util.Map;

public class MqttHandler extends MqttClientHandler {
    @Setter
    @Getter
    private String topic;

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String messageTxt = ByteBufferUtil.toString(payload);

        if (topic.endsWith("/down/device/record/timestamp")) {
            this.handleTimeStamp(messageTxt);
            return;
        }
    }

    private void handleTimeStamp(String messageTxt) {
        try {
            Map<String, Object> map = JsonUtils.buildObject(messageTxt, Map.class);
            String uuid = (String) map.get("uuid");
            if (MethodUtils.hasEmpty(uuid)) {
                return;
            }

            // 将数据转移到队列中，通知发送者查询
            SyncFlagObjectMap.inst().notifyDynamic(uuid, map);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}