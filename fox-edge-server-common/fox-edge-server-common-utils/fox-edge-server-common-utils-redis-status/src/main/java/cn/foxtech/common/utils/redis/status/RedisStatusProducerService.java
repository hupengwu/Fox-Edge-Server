package cn.foxtech.common.utils.redis.status;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;


public abstract class RedisStatusProducerService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Getter
    @Value("${spring.application.name}")
    private String applicationName = "null";
    /**
     * 最近更新时间
     */
    private long lastTime = 0;
    /**
     * 缓存数据
     */
    private Object data;

    public abstract String getKeySync();

    public abstract String getKeyData();

    /**
     * 3、源端（异步线程）：保存数据
     */
    public synchronized void save(String serviceKey) {
        this.lastTime = System.currentTimeMillis();
        this.redisTemplate.opsForHash().put(this.getKeySync(), serviceKey, this.lastTime);
        this.redisTemplate.opsForHash().put(this.getKeyData(), serviceKey, this.data);
    }

    /**
     * 4、消费端（异步线程）：保存数据
     */
    public synchronized void load(String serviceKey) {
        // 读取redis的时间戳
        Long time = (Long) this.redisTemplate.opsForHash().get(this.getKeySync(), serviceKey);
        if (time == null) {
            this.lastTime = 0;
        }

        // 检查：时间戳是否变化
        if (time.equals(this.lastTime)) {
            return;
        }

        // 读取数据
        this.data = this.redisTemplate.opsForHash().get(this.getKeyData(), serviceKey);

        // 更新时间戳
        this.lastTime = time;
    }

    /**
     * 5、消费端（异步线程）：保存数据
     *
     * @return 对象
     */
    public synchronized Object getData() {
        return this.data;
    }

    /**
     * 1、源端（生产者）：把数据保存在缓存
     *
     * @param value
     */
    public synchronized void setData(final Object value) {
        this.data = value;
    }
}
