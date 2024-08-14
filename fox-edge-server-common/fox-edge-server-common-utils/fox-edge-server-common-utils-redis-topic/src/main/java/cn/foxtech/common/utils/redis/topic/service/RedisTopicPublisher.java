/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.redis.topic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 发布者
 */
@Component
public class RedisTopicPublisher {
    @Autowired
    protected RedisTemplate redisTemplate;

    public void sendMessage(String topic, Object message) {
        this.redisTemplate.convertAndSend(topic, message);
    }
}
