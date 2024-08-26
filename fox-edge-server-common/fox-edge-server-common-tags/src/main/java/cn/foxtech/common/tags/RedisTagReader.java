/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.tags;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 读取全局标志
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class RedisTagReader {


    /**
     * redis
     */
    @Autowired
    private RedisTemplate redisTemplate;


    private String getHead() {
        return "fox.edge.tags.";
    }


    /**
     * 读取同步标记
     *
     * @return
     */
    public Object readTagsSync() {
        return this.redisTemplate.opsForValue().get(this.getHead() + "sync");
    }

    public Map<String, Object> readTags() {
        Map<String, Object> dataJsn = this.redisTemplate.opsForHash().entries(this.getHead() + "data");
        return dataJsn;
    }

    public Object readTag(String key) {
        return this.redisTemplate.opsForHash().get(this.getHead() + "data", key);
    }
}
