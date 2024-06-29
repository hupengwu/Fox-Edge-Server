package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListPersistClientRecordRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:persist:record:request";

    @Override
    public void push(Object value) {
        super.push(value);
    }
}