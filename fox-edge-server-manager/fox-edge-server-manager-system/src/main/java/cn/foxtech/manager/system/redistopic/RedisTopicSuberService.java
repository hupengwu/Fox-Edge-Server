package cn.foxtech.manager.system.redistopic;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);

    @Autowired
    private IRedisTopicService redisTopicService;

    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_manager_request + RedisTopicConstant.model_public;
    }

    @Override
    public String topic2nd() {
        return RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager;
    }

    @Override
    public String topic3rd() {
        return RedisTopicConstant.topic_device_respond + RedisTopicConstant.model_manager;
    }

    @Override
    public String topic4th() {
        return RedisTopicConstant.topic_persist_respond + RedisTopicConstant.model_manager;
    }


    /**
     * 接收所有发给manage的单向请求
     *
     * @param message 消息
     */
    @Override
    public void receiveTopic1st(String message) {
        try {
            RestFulRequestVO requestVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            this.redisTopicService.requestManager(requestVO);
        } catch (Exception e) {
            logger.warn(e);
        }

    }

    @Override
    public void receiveTopic2nd(String message) {
        try {
            ChannelRespondVO respondVO = JsonUtils.buildObject(message, ChannelRespondVO.class);
            this.redisTopicService.respondChannel(respondVO);
        } catch (Exception e) {
            logger.warn(e);
        }

    }

    @Override
    public void receiveTopic3rd(String message) {
        try {
            PublicRespondVO respondVO = JsonUtils.buildObject(message, PublicRespondVO.class);
            this.redisTopicService.respondDevice(respondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    @Override
    public void receiveTopic4th(String message) {
        try {
            RestFulRespondVO respondVO = JsonUtils.buildObject(message, RestFulRespondVO.class);
            this.redisTopicService.respondPersist(respondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
