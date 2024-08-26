/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.server;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.redis.list.RedisListService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 高可靠信道：记录类型的队列，这是可靠性记录，它会在redis之中缓存
 * 接收者：persist
 * 发送者：manage
 */
@Component
public class RedisListPersistServerManageRequest extends RedisListService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:request";

    public RestFulRequestVO popRequest(long timeout, TimeUnit unit) {
        try {
            Object map = super.pop(timeout, unit);
            if (map == null) {
                return null;
            }

            return RestFulRequestVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }


}