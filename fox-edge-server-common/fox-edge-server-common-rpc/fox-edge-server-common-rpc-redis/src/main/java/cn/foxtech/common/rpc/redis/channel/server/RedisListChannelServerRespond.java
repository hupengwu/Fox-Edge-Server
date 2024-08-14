/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.channel.server;

import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 记录类型的队列：这是可靠性记录，它会在redis之中缓存
 */
@Component
public class RedisListChannelServerRespond extends RedisValueService {
    private final String key = "fox.edge.list:channel:";
    @Setter(value = AccessLevel.PUBLIC)
    private String channelType;

    public String getKey() {
        return this.key + this.channelType + ":respond";
    }

    @Override
    public void set(String hashKey, Object value) {
        super.set(hashKey, value);
    }
}