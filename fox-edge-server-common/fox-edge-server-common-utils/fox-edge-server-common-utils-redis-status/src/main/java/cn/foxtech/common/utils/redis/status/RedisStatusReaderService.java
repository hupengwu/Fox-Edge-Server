/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.redis.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

public abstract class RedisStatusReaderService {
    @Autowired
    private RedisTemplate redisTemplate;

    public abstract String getKeyData();

    /**
     * 5、消费端（异步线程）：保存数据
     *
     * @return 状态
     */
    public Map<String, Object> getStatus() {
        return (Map<String, Object>) redisTemplate.opsForHash().entries(this.getKeyData());
    }
}
