package cn.foxtech.device.service.redistopic;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.vo.TaskRequestVO;
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
        return RedisTopicConstant.topic_device_request + RedisTopicConstant.model_public;
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
                SyncQueueObjectMap.inst().push(RedisTopicConstant.model_channel, respondVO, 1000);
            }
        } catch (Exception e) {
            logger.info(e);
        }

    }

    @Override
    public void receiveTopic2nd(String message) {
        try {
            TaskRequestVO taskRequestVO = JsonUtils.buildObject(message, TaskRequestVO.class);
            SyncQueueObjectMap.inst().push(RedisTopicConstant.model_device, taskRequestVO, 1000);
        } catch (Exception e) {
            logger.info("接收到的报文格式不正确，它不是一个合法的包裹：" + e.getMessage());
        }
    }
}
