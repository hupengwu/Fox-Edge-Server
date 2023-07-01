package cn.foxtech.common.status;

import cn.foxtech.common.utils.redis.status.RedisStatusConsumerService;
import org.springframework.stereotype.Component;

@Component
public class ServiceStatusConsumerService extends RedisStatusConsumerService {
    public String getKeySync(){
        return "fox.edge.service.status.sync";
    }
    public String getKeyData(){
        return "fox.edge.service.status.data";
    }
}
