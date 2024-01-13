package cn.foxtech.persist.common.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 接收列表
 * 接收者：manage
 * 发送者：persist
 */
@Component
public class RedisListManageRespond extends RedisLoggerService {
    public RedisListManageRespond() {
        this.setKey("fox.edge.list.manage.persist");
    }

    @Override
    public void push(Object value) {
        super.push(value);
    }
}