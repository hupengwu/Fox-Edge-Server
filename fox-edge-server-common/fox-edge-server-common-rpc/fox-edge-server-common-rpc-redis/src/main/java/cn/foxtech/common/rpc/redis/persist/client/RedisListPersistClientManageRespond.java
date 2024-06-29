package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 接收列表
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RedisListPersistClientManageRespond extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:respond";

    public RestFulRespondVO popRespond(long timeout, TimeUnit unit) {
        try {
            Object map = super.pop(timeout, unit);
            if (map == null) {
                return null;
            }

            return RestFulRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}