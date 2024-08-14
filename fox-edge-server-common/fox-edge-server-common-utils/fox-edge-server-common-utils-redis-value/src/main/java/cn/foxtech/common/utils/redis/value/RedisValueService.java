/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.redis.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public abstract class RedisValueService {
    @Autowired
    private RedisTemplate redisTemplate;
    private long timeout = 60 * 1000L;

    public abstract String getKey();

    protected void set(String hashKey, Object value) {
        if (hashKey == null || hashKey.isEmpty() || value == null) {
            return;
        }

        // 临时key
        String mainKey = this.getKey() + ":" + hashKey;

        this.redisTemplate.opsForValue().set(mainKey, value);
        this.expire(mainKey, this.timeout);
    }

    protected Object get(String hashKey, long timeout) {
        String mainKey = this.getKey() + ":" + hashKey;
        return this.getValue(mainKey, timeout);
    }

    protected Object get(String prefixKey, String hashKey, long timeout) {
        String mainKey = prefixKey + ":" + hashKey;
        return this.getValue(mainKey, timeout);
    }

    protected Object getValue(String mainKey, long timeout) {
        try {
            long start = System.currentTimeMillis();
            while (true) {
                Object data = this.redisTemplate.opsForValue().get(mainKey);
                if (data != null) {
                    this.expire(mainKey, 0);
                    return data;
                }

                if (System.currentTimeMillis() - start > timeout) {
                    return null;
                }

                Thread.sleep(5);
            }
        } catch (Exception e) {
            return null;
        }
    }

    protected void expire(String mainKey, long timeout) {
        if (this.timeout == -1L) {
            return;
        }

        this.redisTemplate.expire(mainKey, timeout, TimeUnit.MILLISECONDS);
    }

}