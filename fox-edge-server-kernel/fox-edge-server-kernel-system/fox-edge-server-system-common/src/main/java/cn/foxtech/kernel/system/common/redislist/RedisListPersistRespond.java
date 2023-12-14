package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 接收列表
 */
@Component
public class RedisListPersistRespond extends RedisLoggerService {
    public RedisListPersistRespond() {
        this.setKey("fox.edge.list.manage.persist");
    }
}