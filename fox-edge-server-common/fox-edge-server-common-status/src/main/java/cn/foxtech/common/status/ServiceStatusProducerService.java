package cn.foxtech.common.status;

import cn.foxtech.common.utils.redis.status.RedisStatusProducerService;
import org.springframework.stereotype.Component;

@Component
public class ServiceStatusProducerService extends RedisStatusProducerService {
    public String getKeySync(){
        return "fox.edge.service.status.sync";
    }
    public String getKeyData(){
        return "fox.edge.service.status.data";
    }
}
