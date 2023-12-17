package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 发送列表
 */
@Component
public class RedisListPersistManageRequest extends RedisLoggerService {
    public RedisListPersistManageRequest() {
        this.setKey("fox.edge.list.persist.manage.request");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}