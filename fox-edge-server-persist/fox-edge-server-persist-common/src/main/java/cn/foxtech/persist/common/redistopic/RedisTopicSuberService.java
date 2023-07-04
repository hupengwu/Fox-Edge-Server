package cn.foxtech.persist.common.redistopic;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);


    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_persist_request + RedisTopicConstant.model_public;
    }

    @Override
    public String topic2nd() {
        return RedisTopicConstant.topic_persist_request + RedisTopicConstant.model_manager;
    }

    @Override
    public void receiveTopic1st(String message) {
        //logger.debug("receive:" + message);

        try {
            TaskRespondVO taskRespondVO = JsonUtils.buildObject(message, TaskRespondVO.class);
            if (taskRespondVO == null) {
                return;
            }

            SyncQueueObjectMap.inst().push(DeviceMethodVOFieldConstant.value_operate_report, taskRespondVO, 256);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    @Override
    public void receiveTopic2nd(String message) {
        //logger.debug("receive:" + message);

        try {
            RestFulRequestVO requestVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            if (requestVO == null) {
                return;
            }

            SyncQueueObjectMap.inst().push(RestFulManagerVOConstant.restful_manager, requestVO, 256);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
