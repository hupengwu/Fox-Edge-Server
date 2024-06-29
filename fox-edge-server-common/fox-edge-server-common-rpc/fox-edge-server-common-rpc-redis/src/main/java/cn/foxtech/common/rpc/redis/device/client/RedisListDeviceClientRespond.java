package cn.foxtech.common.rpc.redis.device.client;

import cn.foxtech.common.utils.redis.value.RedisValueService;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDeviceClientRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:device:task:respond";

    public TaskRespondVO get(String hashKey, long timeout) {
        try {
            Object map = super.get(hashKey, timeout);
            if (map == null) {
                return null;
            }

            return TaskRespondVO.buildRespondVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}