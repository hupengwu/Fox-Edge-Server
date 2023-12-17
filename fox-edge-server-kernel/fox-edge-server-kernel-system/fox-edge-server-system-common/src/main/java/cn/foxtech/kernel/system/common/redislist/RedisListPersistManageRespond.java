package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 接收列表
 */
@Component
public class RedisListPersistManageRespond extends RedisLoggerService {
    public RedisListPersistManageRespond() {
        this.setKey("fox.edge.list.persist.manage.respond");
    }
}