/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.redis.list;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 先进先出的队列
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public abstract class RedisListService {
    @Autowired
    private RedisTemplate redisTemplate;


    private long maxSize = 1000L;

    protected abstract String getKey();

    /**
     * 保存数据
     *
     * @param list 列表数据
     */
    protected void pushAll(List list) {
        // 左边插入数据
        this.redisTemplate.opsForList().rightPushAll(this.getKey(), list);

        // 删除溢出
        this.removeOverflow(this.getKey());
    }

    protected void push(Object value) {
        // 左边插入数据
        this.redisTemplate.opsForList().rightPush(this.getKey(), value);

        // 删除溢出
        this.removeOverflow(this.getKey());
    }

    protected void push(String key, Object value) {
        // 左边插入数据
        this.redisTemplate.opsForList().rightPush(key, value);

        // 删除溢出
        this.removeOverflow(key);
    }

    /**
     * redis 6.20之前的版本，不支持批量删除
     */
    private void removeOverflow(String key) {
        while (this.redisTemplate.opsForList().size(key) > this.maxSize) {
            this.redisTemplate.opsForList().leftPop(key);
        }
    }


    public Long size() {
        return this.redisTemplate.opsForList().size(this.getKey());
    }

    public boolean isBlock() {
        return this.redisTemplate.opsForList().size(this.getKey()) > this.maxSize;
    }

    /**
     * 是否处于繁忙
     *
     * @param percentage 百分比，例如40，表示40%
     * @return 是否繁忙
     */
    public boolean isBusy(int percentage) {
        percentage = percentage > 100 ? 100 : percentage;
        percentage = percentage < 10 ? 10 : percentage;

        return this.redisTemplate.opsForList().size(this.getKey()) > this.maxSize * percentage / 100;
    }

    /**
     * 弹出一个对象
     * 此时，这个对象会从redis的列表中删除
     *
     * @return 列表中的对象
     */
    public Object pop() {
        return this.redisTemplate.opsForList().leftPop(this.getKey());
    }

    /**
     * 等待一个对象的到达
     * 此时，这个对象会从redis的列表中删除
     *
     * @param timeout 等待超时
     * @param unit    等待超时，时间单位
     * @return
     */
    public Object pop(long timeout, TimeUnit unit) {
        return this.redisTemplate.opsForList().leftPop(this.getKey(), timeout, unit);
    }

    /**
     * 预览一个对象
     * 此时，这个对象依然在redis的列表中
     * <p>
     * 说明：一般的做法，是想rang一下有没有数据，用完之后，在pop掉这个对象
     *
     * @return 列表中的对象
     */
    public Object rangeOne() {
        List<Object> list = this.redisTemplate.opsForList().range(this.getKey(), 0L, 1);
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }


    public <V> List<V> range() {
        return this.redisTemplate.opsForList().range(this.getKey(), 0L, this.maxSize);
    }

    public <V> List<V> range(Long start, Long end) {
        return this.redisTemplate.opsForList().range(this.getKey(), start, end);
    }
}
