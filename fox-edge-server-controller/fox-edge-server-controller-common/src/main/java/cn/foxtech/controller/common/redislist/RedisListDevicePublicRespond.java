package cn.foxtech.controller.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;


/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDevicePublicRespond extends RedisLoggerService {
    public RedisListDevicePublicRespond() {
        this.setKey("fox.edge.list.device.public.respond");
    }
}