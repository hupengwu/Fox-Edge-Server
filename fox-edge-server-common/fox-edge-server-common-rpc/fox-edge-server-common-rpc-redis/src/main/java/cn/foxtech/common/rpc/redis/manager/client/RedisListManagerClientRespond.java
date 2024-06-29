package cn.foxtech.common.rpc.redis.manager.client;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListManagerClientRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:manager:restful:message:respond";

    /**
     * 查询服务端响应
     *
     * @param uuid
     * @param timeout 超时，单位秒
     * @return
     */
    public RestFulRespondVO queryRespond(String uuid, long timeout) {
        try {
            Object map = super.get(uuid, timeout);
            if (map == null) {
                return null;
            }

            return RestFulRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }

}