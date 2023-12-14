package cn.foxtech.controller.common.redistopic;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisTopicPuberService {
    private static final Logger logger = Logger.getLogger(RedisTopicPuberService.class);
    /**
     * 设备服务的Topic
     */
    private final String topic_device_request = RedisTopicConstant.topic_device_request + RedisTopicConstant.model_public;

    /**
     * 发送者
     */
    @Autowired
    private RedisTopicPublisher publisher;


    public void sendRequestVO(TaskRequestVO operateRespondVO) {
        String json = JsonUtils.buildJsonWithoutException(operateRespondVO);
        this.publisher.sendMessage(topic_device_request, json);
    }


    /**
     * 发送响应报文
     *
     * @param respondVO 发送回复报文
     */
    public void sendRespondVO(Map<String, Object> respondVO) {
        String body = JsonUtils.buildJsonWithoutException(respondVO);
        publisher.sendMessage(topic_device_request, body);
    }
}
