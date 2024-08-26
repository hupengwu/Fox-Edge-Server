/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.utils.redis.list.RedisListService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 发送列表
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RedisListPersistClientManageRequest extends RedisListService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:request";

    @Override
    public void push(Object value) {
        super.push(value);
    }
}