package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.proxy.client.constants.Constant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);

    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device;
    }

    @Override
    public String topic2nd() {
        return RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager;
    }

    @Override
    public void receiveTopic1st(String message) {
        try {
            ChannelRespondVO respondVO = JsonUtils.buildObject(message, ChannelRespondVO.class);
            if (respondVO.getUuid() != null && !respondVO.getUuid().isEmpty()) {
                // 带UUID报文：这是Exchange需要的的主从应答报文
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), message);
            } else {
                // 不带UUID报：这是Subscribe需要的主动上报报文
                SyncQueueObjectMap.inst().push(Constant.SYSNC_CHANNEL, respondVO, 100);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    @Override
    public void receiveTopic2nd(String message) {
        try {
            ChannelRespondVO respondVO = JsonUtils.buildObject(message, ChannelRespondVO.class);
            if (respondVO.getUuid() != null && !respondVO.getUuid().isEmpty()) {
                // 带UUID报文：这是Exchange需要的的主从应答报文
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), message);
            } else {
                // 不带UUID报：这是Subscribe需要的主动上报报文
                SyncQueueObjectMap.inst().push(Constant.SYSNC_CHANNEL, respondVO, 100);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

}
