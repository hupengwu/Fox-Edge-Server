package cn.foxtech.common.status;

import cn.foxtech.common.utils.redis.status.RedisStatusProducerService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServiceStatusProducerService extends RedisStatusProducerService {
    /**
     * 本地缓存
     */
    @Getter(value = AccessLevel.PUBLIC)
    private final Map<String, Object> status = new ConcurrentHashMap<>();

    public String getKeySync() {
        return "fox.edge.service.status.sync";
    }

    public String getKeyData() {
        return "fox.edge.service.status.data";
    }
}
