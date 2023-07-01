package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 主从问答方式设备的数据采集<br>
 * 背景知识：主从半双工设备，这类设备只会被动响应上位机的命令请求。现实中大多数简单的工控设备都是这种设备<br>
 */
@Component
public class RedisTopicPuberService {
    @Autowired
    private RedisTopicPublisher publisher;

    /**
     * 发送报文
     *
     * @return 响应报文
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws InterruptedException, TimeoutException, IOException {
        // 填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        if (requestVO.getUuid() == null || requestVO.getUuid().isEmpty()) {
            requestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        }

        // 重新打包
        String body = JsonUtils.buildJson(requestVO);

        // 通道类型所属的topic
        String topic = RedisTopicConstant.topic_channel_request + requestVO.getType();


        // 重置信号
        String key = requestVO.getUuid();
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        publisher.sendMessage(topic, body);

        // 等待消息的到达：根据动态key
        String respond = (String) SyncFlagObjectMap.inst().waitDynamic(key, requestVO.getTimeout());
        if (respond == null) {
            throw new TimeoutException("设备响应超时！");
        }

        return JsonUtils.buildObject(respond, ChannelRespondVO.class);
    }
}
