/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.server;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * 接收列表
 * 接收者：manage
 * 发送者：persist
 */
@Component
public class RedisListPersistServerManageRespond extends RedisValueService {
    @Getter
    private final String key = "fox-edge:list:persist:manage:respond";

    public void pushRespond(RestFulRespondVO value) {
        super.set(value.getUuid(), value);
    }
}