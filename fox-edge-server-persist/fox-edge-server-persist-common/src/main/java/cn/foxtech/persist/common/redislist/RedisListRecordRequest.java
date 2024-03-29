package cn.foxtech.persist.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 高可靠信道：记录类型的队列，这是可靠性记录，它会在redis之中缓存
 * 接收者：persist
 * 发送者：其他服刑
 */
@Component
public class RedisListRecordRequest extends RedisLoggerService {
    public RedisListRecordRequest() {
        this.setKey("fox.edge.list.persist.record");
    }
}