package cn.foxtech.common.rpc.redis.persist.server;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 高可靠信道：记录类型的队列，这是可靠性记录，它会在redis之中缓存
 * 接收者：persist
 * 发送者：其他服刑
 */
@Component
public class RedisListPersistServerRecordRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:persist:record:request";
}