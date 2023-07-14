package cn.foxtech.controller.common.redistopic;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);

    @Value("${spring.redis_topic.controller_model}")
    private String controller_model = "system_controller";

    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_device_respond + this.controller_model;
    }

    @Override
    public String topic2nd() {
        return RedisTopicConstant.topic_device_respond + RedisTopicConstant.model_public;
    }

    @Override
    public void receiveTopic1st(String message) {
        //logger.debug("receive:" + message);

        try {
            TaskRespondVO respondVO = JsonUtils.buildObject(message, TaskRespondVO.class);
            String key = respondVO.getUuid();
            if (key == null || key.isEmpty()) {
                logger.info("接收到的报文格式不正确，只要要包含uuid：" + message);
                return;
            }

            SyncFlagObjectMap.inst().notifyDynamic(key, respondVO);

        } catch (Exception e) {
            logger.warn(e);
        }

    }

    @Override
    public void receiveTopic2nd(String message) {
        //logger.debug("receive:" + message);

        try {
            TaskRespondVO respondVO = JsonUtils.buildObject(message, TaskRespondVO.class);
            for (OperateRespondVO operateRespondVO : respondVO.getRespondVOS()) {
                String operateMode = operateRespondVO.getOperateMode();
                if (operateMode == null) {
                    logger.debug("接收到的报文格式不正确：" + message);
                    continue;
                }

                // 捕获的是设备主动上报
                if (DeviceMethodVOFieldConstant.value_operate_report.equals(operateMode)) {
                    SyncQueueObjectMap.inst().push(DeviceMethodVOFieldConstant.value_operate_report, operateRespondVO, 1000);
                    continue;
                }
                // 捕获的是操作记录
                if (DeviceMethodVOFieldConstant.value_operate_exchange.equals(operateMode)) {
                    SyncQueueObjectMap.inst().push(DeviceMethodVOFieldConstant.value_operate_exchange, operateRespondVO, 1000);
                    continue;
                }
            }

        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
