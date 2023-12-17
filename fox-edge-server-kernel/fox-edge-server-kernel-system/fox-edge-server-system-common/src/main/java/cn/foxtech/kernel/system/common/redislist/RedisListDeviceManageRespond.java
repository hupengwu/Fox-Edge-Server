package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;


/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDeviceManageRespond extends RedisLoggerService {
    public RedisListDeviceManageRespond() {
        this.setKey("fox.edge.list.device.manager.respond");
    }
}