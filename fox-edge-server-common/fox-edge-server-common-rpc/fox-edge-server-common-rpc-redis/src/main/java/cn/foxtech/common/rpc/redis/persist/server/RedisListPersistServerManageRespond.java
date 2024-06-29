package cn.foxtech.common.rpc.redis.persist.server;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 接收列表
 * 接收者：manage
 * 发送者：persist
 */
@Component
public class RedisListPersistServerManageRespond extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:respond";

    @Override
    public void push(Object value) {
        super.push(value);
    }

}