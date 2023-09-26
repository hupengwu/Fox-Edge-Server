package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.proxy.client.constants.Constant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class MqttMessageReporter {

    private String topic_head = "/foxteam/v1/proxy/response/#";
    @Autowired
    private MqttClientService clientService;

    public void reportMessage() {
        try {
            List<Object> objectList = SyncQueueObjectMap.inst().popup(Constant.SYSNC_CHANNEL);
            for (Object object : objectList) {
                ChannelRespondVO respondVO = (ChannelRespondVO) object;

                // 构造发送到云端服务器的topic
                StringBuilder stringBuilder = new StringBuilder();
                if (this.topic_head.endsWith("#")) {
                    stringBuilder.append(this.topic_head, 0, this.topic_head.length() - 1);
                    stringBuilder.append(respondVO.getType());
                } else {
                    stringBuilder.append(this.topic_head);
                    stringBuilder.append(respondVO.getType());
                }
                String rspTopic = stringBuilder.toString();

                String rspContext = JsonUtils.buildJson(respondVO);

                this.clientService.getMqttClient().publish(rspTopic, rspContext.getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
