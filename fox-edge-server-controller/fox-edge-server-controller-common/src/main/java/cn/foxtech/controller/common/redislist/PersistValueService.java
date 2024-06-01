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

        // 作为生产者，将Redis的日志队列大小填为4096个消息，毕竟数值数据还是非常多的
        this.setMaxSize(4096);
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}