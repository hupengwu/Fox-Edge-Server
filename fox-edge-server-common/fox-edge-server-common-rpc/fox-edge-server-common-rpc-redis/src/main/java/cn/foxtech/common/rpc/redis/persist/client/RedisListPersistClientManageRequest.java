package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 发送列表
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RedisListPersistClientManageRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:request";

    @Override
    public void push(Object value) {
        super.push(value);
    }
}