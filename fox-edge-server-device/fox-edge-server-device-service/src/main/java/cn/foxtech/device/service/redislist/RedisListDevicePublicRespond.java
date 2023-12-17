package cn.foxtech.device.service.redislist;

import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDevicePublicRespond extends RedisLoggerService {
    @Autowired
    private ServiceStatus serviceStatus;

    public RedisListDevicePublicRespond() {
        this.setKey("fox.edge.list.device.public.respond");
    }

    public void push(Object value) {
        Map<String, Object> controllerMap = (Map<String, Object>) this.serviceStatus.getModelStatus().get("controller");
        for (String key : controllerMap.keySet()) {
            Object time = controllerMap.get(key);
            if (time == null) {
                continue;
            }

            boolean isActive = false;
            if (time instanceof Long) {
                isActive = System.currentTimeMillis() - (Long) time > 60 * 1000;
            }
            if (time instanceof Integer) {
                isActive = System.currentTimeMillis() - (Integer) time > 60 * 1000;
            }
            if (!isActive) {
                continue;
            }

            super.push("fox.edge.list.device." + key + ".respond", value);
        }

    }
}