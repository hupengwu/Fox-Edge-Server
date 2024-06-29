package cn.foxtech.common.rpc.redis.channel.client;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelClientRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:channel:";

    public ChannelRespondVO get(String channelType, String hashKey, long timeout) {
        try {
            Object map = super.get(this.key + channelType + ":respond", hashKey, timeout);
            if (map == null) {
                return null;
            }

            return ChannelRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}