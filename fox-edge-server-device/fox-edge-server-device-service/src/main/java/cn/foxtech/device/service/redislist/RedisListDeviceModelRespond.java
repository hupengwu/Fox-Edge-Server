package cn.foxtech.device.service.redislist;

import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListDeviceModelRespond extends RedisLoggerService {
    private final String head = "fox.edge.list.device.";

    public RedisListDeviceModelRespond() {
        this.setKey("fox.edge.list.device.public.respond");
    }

    public void push(String model, Object value) {
        if (model == null || model.isEmpty()) {
            super.push(this.head + "public.respond", value);
        } else {
            super.push(this.head + model + ".respond", value);
        }
    }
}