package cn.foxtech.proxy.cloud.forwarder.service.proxy;

import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicSubscriber;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Redis Topic的订阅：监听device和channel返回来的消息
 */
@Component
public class RedisTopicSuberService extends RedisTopicSubscriber {
    private static final Logger logger = Logger.getLogger(RedisTopicSuberService.class);

    @Override
    public String topic1st() {
        return RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_device;
    }

    @Override
    public String topic2nd() {
        return RedisTopicConstant.topic_device_respond + RedisTopicConstant.model_proxy4cloud2topic;
    }

    @Override
    public void receiveTopic1st(String message) {
        try {
            Map<String, Object> map = JsonUtils.buildObject(message, Map.class);
            String key = (String) map.get(DeviceMethodVOFieldConstant.field_uuid);
            if (key != null && !key.isEmpty()) {
                SyncFlagObjectMap.inst().notifyDynamic(key, message);
            }
        } catch (IOException e) {
            logger.warn(e.toString());
        }
    }

    @Override
    public void receiveTopic2nd(String message) {
        try {
            Map<String, Object> map = JsonUtils.buildObject(message, Map.class);
            String key = (String) map.get(DeviceMethodVOFieldConstant.field_uuid);
            if (key != null && !key.isEmpty()) {
                SyncFlagObjectMap.inst().notifyDynamic(key, message);
            }
        } catch (IOException e) {
            logger.warn(e.toString());
        }
    }
}
