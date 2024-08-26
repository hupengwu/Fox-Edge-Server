/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.channel.client;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.redis.list.RedisListService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelClientReport extends RedisListService {
    @Getter
    private final String key = "fox.edge.list:channel:report";

    @Override
    public ChannelRespondVO pop(long timeout, TimeUnit unit) {
        try {
            Object map = super.pop(timeout, unit);
            if (map == null) {
                return null;
            }

            return ChannelRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }

    }
}