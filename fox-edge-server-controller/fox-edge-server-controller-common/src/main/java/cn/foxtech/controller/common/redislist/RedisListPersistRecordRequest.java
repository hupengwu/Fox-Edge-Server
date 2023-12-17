package cn.foxtech.controller.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListPersistRecordRequest extends RedisLoggerService {
    public RedisListPersistRecordRequest() {
        this.setKey("fox.edge.list.persist.record.request");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}