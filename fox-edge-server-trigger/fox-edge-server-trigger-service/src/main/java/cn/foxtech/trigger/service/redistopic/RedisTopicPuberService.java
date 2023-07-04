package cn.foxtech.trigger.service.redistopic;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicPuberService {
    /**
     * 持久化服务的Topic
     */
    private final String topic_trigger_respond = RedisTopicConstant.topic_trigger_respond + RedisTopicConstant.model_manager;
    /**
     * 发送者
     */
    @Autowired
    private RedisTopicPublisher publisher;

    public void sendRespondVO(RestFulRespondVO respondVO) {
        String json = JsonUtils.buildJsonWithoutException(respondVO);
        this.publisher.sendMessage(topic_trigger_respond, json);
    }

}
