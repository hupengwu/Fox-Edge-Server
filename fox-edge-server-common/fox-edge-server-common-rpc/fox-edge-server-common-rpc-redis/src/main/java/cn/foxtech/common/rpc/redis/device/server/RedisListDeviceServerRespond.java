package cn.foxtech.common.rpc.redis.device.server;

import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDeviceServerRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:device:task:respond";

    @Override
    public void set(String hashKey, Object value) {
        super.set(hashKey, value);
    }
}