package cn.foxtech.common.utils.redis.logger;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RedisLoggerService {
    @Autowired
    private RedisTemplate redisTemplate;

    private String key;

    private Long maxSize = 1000L;

    /**
     * 保存数据
     *
     * @param list 列表数据
     */
    protected void out(List list) {
        this.redisTemplate.opsForList().leftPushAll(this.key, list);

        // redis 6.20之前的版本，不支持批量删除
        while (this.redisTemplate.opsForList().size(this.key) > this.maxSize){
            this.redisTemplate.opsForList().rightPop(this.key);
        }
    }

    public Long size() {
        return this.redisTemplate.opsForList().size(this.key);
    }

    public <V> List<V> range() {
        return this.redisTemplate.opsForList().range(this.key, 0L, this.maxSize);
    }

    public <V> List<V> range(Long start, Long end) {
        return this.redisTemplate.opsForList().range(this.key, start, end);
    }
}
