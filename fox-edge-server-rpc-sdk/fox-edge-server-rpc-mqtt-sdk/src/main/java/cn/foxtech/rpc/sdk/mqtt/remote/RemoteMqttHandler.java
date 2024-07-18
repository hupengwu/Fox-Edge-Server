package cn.foxtech.rpc.sdk.mqtt.remote;

import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.mqtt.MqttClientHandler;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import org.tio.core.ChannelContext;
import org.tio.utils.buffer.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * 默认的handler实现
 */
public class RemoteMqttHandler extends MqttClientHandler {
    @Setter
    @Getter
    private String topic = "/fox/manager/e2c/forward/#";

    @Override
    public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
        String json = ByteBufferUtil.toString(payload);

        this.notifyRespond(topic, json);
    }

    private void notifyRespond(String topic, String json) {
        try {
            RestfulLikeRespondVO respondVO = JsonUtils.buildObject(json, RestfulLikeRespondVO.class);
            if (respondVO.getUuid() == null || respondVO.getUuid().isEmpty()) {
                return;
            }

            // 把topic修改为实际接收的topic
            respondVO.setTopic(topic);

            // 将数据转移到队列中，通知发送者查询
            SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
        } catch (Exception e) {
            e.getMessage();
        }
    }
}