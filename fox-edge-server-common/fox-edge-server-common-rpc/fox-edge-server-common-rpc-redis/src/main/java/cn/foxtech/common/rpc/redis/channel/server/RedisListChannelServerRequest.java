package cn.foxtech.common.rpc.redis.channel.server;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelServerRequest extends RedisLoggerService {
    private final String key = "fox.edge.list:channel:";

    @Setter(value = AccessLevel.PUBLIC)
    private String channelType;

    public String getKey() {
        return this.key + this.channelType + ":request";
    }

    @Override
    public ChannelRequestVO pop(long timeout, TimeUnit unit) {
        try {
            Object map = super.pop(timeout, unit);
            if (map == null) {
                return null;
            }

            return ChannelRequestVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}