package cn.foxtech.common.utils.redis.status;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class RedisStatusConsumerService {
    /**
     * 本地缓存
     */
    private final Map<String, Status> localMap = new ConcurrentHashMap<>();
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 是否已经完成初始化
     */
    private boolean inited = false;


    /**
     * 系统key
     * @return 系统key
     */
    public abstract String getKeySync();

    public abstract String getKeyData();

    public boolean isInited() {
        return this.inited;
    }

    /**
     * 4、消费端（异步线程）：保存数据
     *
     * @return 是否成功
     */
    public synchronized boolean load() {
        // 读取redis数据
        Map<String, Long> redisSync = redisTemplate.opsForHash().entries(this.getKeySync());

        for (String key : redisSync.keySet()) {
            Long updateTime = redisSync.get(key);

            // 取出本地缓存的数据
            Status localStatus = this.localMap.get(key);
            if (localStatus == null) {
                localStatus = new Status();
                this.localMap.put(key, localStatus);
            }

            // 比较时间戳
            if (localStatus.lastTime == updateTime) {
                continue;
            }

            // 读取redis数据
            localStatus.data = redisTemplate.opsForHash().get(this.getKeyData(), key);

            // 更新读取数据
            localStatus.lastTime = updateTime;
        }

        this.inited = true;
        return true;
    }

    /**
     * 5、消费端（异步线程）：保存数据
     *
     * @return 状态
     */
    public synchronized Map<String, Status> getStatus() {
        return this.localMap;
    }


    @Getter(value = AccessLevel.PUBLIC)
    @Setter(value = AccessLevel.PUBLIC)
    static public class Status implements Serializable {
        /**
         * 最近更新时间
         */
        private long lastTime = 0;

        /**
         * 缓存数据
         */
        private Object data = "";
    }
}
