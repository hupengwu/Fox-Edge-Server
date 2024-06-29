package cn.foxtech.common.rpc.redis.channel.client;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;


/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelClientRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:channel:";

    @Override
    public void push(String channelType, Object value) {
        super.push(this.key + channelType + ":request", value);
    }
}