package cn.foxtech.trigger.service.redistopic;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);


    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_trigger_request + RedisTopicConstant.model_manager;
    }

    @Override
    public void receiveTopic1st(String message) {
        //logger.debug("receive:" + message);

        try {
            RestFulRequestVO requestVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            if (requestVO == null) {
                return;
            }

            SyncQueueObjectMap.inst().push(RestFulManagerVOConstant.restful_manager, requestVO, 1000);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
