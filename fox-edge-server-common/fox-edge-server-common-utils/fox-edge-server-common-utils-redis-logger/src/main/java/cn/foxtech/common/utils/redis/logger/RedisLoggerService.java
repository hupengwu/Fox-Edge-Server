package cn.foxtech.common.utils.redis.logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 先进先出的队列
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RedisLoggerService {
    @Autowired
    private RedisTemplate redisTemplate;

    private String key;

    private long maxSize = 1000L;

    /**
     * 保存数据
     *
     * @param list 列表数据
     */
    protected void pushAll(List list) {
        // 左边插入数据
        this.redisTemplate.opsForList().rightPushAll(this.key, list);

        // 删除溢出
        this.removeOverflow();
    }

    protected void push(Object value) {
        // 左边插入数据
        this.redisTemplate.opsForList().rightPush(this.key, value);

        // 删除溢出
        this.removeOverflow();
    }

    /**
     * redis 6.20之前的版本，不支持批量删除
     */
    private void removeOverflow() {
        while (this.redisTemplate.opsForList().size(this.key) > this.maxSize) {
            this.redisTemplate.opsForList().leftPop(this.key);
        }
    }


    public Long size() {
        return this.redisTemplate.opsForList().size(this.key);
    }

    public boolean isBlock() {
        return this.redisTemplate.opsForList().size(this.key) > this.maxSize;
    }

    /**
     * 弹出一个对象
     * 此时，这个对象会从redis的列表中删除
     *
     * @return 列表中的对象
     */
    public Object pop() {
        return this.redisTemplate.opsForList().leftPop(this.key);
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
        List<Object> list = this.redisTemplate.opsForList().range(this.key, 0L, 1);
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }


    public <V> List<V> range() {
        return this.redisTemplate.opsForList().range(this.key, 0L, this.maxSize);
    }

    public <V> List<V> range(Long start, Long end) {
        return this.redisTemplate.opsForList().range(this.key, start, end);
    }
}
