/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.manager.server;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListManagerServerRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:manager:restful:message:respond";

    public void pushRespond(String uuid, RestFulRespondVO value) {
        super.set(uuid, value);
    }
}