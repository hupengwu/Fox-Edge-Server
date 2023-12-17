package cn.foxtech.controller.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 高可靠信道：记录类型的队列，这是可靠性记录，它会在redis之中缓存
 */
@Component
public class PersistValueService extends RedisLoggerService {
    public PersistValueService() {
        this.setKey("fox.edge.list.persist.value");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}