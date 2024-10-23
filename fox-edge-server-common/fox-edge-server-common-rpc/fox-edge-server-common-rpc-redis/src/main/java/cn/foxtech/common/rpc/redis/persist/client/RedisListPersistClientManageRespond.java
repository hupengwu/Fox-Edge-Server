/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 接收列表
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RedisListPersistClientManageRespond extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:persist:manage:respond";

    public RestFulRespondVO get(String hashKey, long timeout) {
        try {
            Object map = super.get(hashKey, timeout);
            if (map == null) {
                return null;
            }

            return RestFulRespondVO.buildVO((Map<String, Object>) map);
        } catch (Exception e) {
            return null;
        }
    }
}