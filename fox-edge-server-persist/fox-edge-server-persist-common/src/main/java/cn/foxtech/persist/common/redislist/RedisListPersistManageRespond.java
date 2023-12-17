package cn.foxtech.persist.common.redislist;

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

    @Override
    public void push(Object value) {
        super.push(value);
    }
}