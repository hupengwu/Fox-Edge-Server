package cn.foxtech.common.status;

import cn.foxtech.common.utils.redis.status.RedisStatusReaderService;
import org.springframework.stereotype.Component;

@Component
public class ServiceStatusReaderService extends RedisStatusReaderService {
    public String getKeySync() {
        return "fox.edge.service.status.sync";
    }

    public String getKeyData() {
        return "fox.edge.service.status.data";
    }
}
