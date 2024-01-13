package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * Restful风格的可靠性列表：无应答，也就是不会对发送者进行回复
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RedisListRestfulMessage extends RedisLoggerService {
    public RedisListRestfulMessage() {
        this.setKey("fox.edge.list.manager.restful.message");
    }
}