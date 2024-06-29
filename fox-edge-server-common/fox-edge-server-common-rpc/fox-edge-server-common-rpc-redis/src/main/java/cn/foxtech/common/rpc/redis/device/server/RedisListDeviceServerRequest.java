package cn.foxtech.common.rpc.redis.device.server;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDeviceServerRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:device:task:request";

    @Override
    public TaskRequestVO pop(long timeout, TimeUnit unit) {
        try {
            Object map = super.pop(timeout, unit);
            if (map == null) {
                return null;
            }

            return TaskRequestVO.buildRequestVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}