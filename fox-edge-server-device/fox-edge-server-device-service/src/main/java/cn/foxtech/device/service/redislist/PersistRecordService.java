package cn.foxtech.device.service.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class PersistRecordService extends RedisLoggerService {
    public PersistRecordService() {
        this.setKey("fox.edge.list.persist.record");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}