package cn.foxtech.kernel.system.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 发送列表
 */
@Component
public class RedisListPersistRequest extends RedisLoggerService {
    public RedisListPersistRequest() {
        this.setKey("fox.edge.list.persist.manage");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}