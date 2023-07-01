package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.util.Map;

/**
 * Redis数据的消费者：
 * 1、消费者会使用isNeedLoad去redis服务器上去查询，上游的生产者是否发布了数据更新到redis中
 * 2、消费者发现有数据更新，那么就从redis服务器loadEntities数据到自己的内存中
 */
public abstract class BaseConsumerRedisService extends BaseRedisService {
    /**
     * 根据redis的时间戳，判定数据是否已经被生产者更新
     *
     * @return
     */
    @Override
    public boolean isNeedLoad() {
        return super.isNeedLoad();
    }

    /**
     * 从redis重新读取数据到本地内存中
     */
    @Override
    public void loadAllEntities() throws JsonParseException {
        super.loadAllEntities();
    }

    /**
     * 绑定通知
     *
     * @param notify
     */
    @Override
    public void bind(BaseConsumerNotify notify) {
        super.bind(notify);
    }

    /**
     * 从redis重新读取数据到本地内存中
     */
    @Override
    public void loadAgileEntities() throws IOException {
        super.loadAgileEntities();
    }


    /**
     * 获得一个副本
     *
     * @return
     */
    @Override
    public Map<String, BaseEntity> getEntitys() {
        return super.getEntitys();
    }

    /**
     * 获取实体
     *
     * @return 实体
     */
    @Override
    public BaseEntity getEntity(String entityKey) {
        return super.getEntity(entityKey);
    }

    /**
     * 根据Key特征，查询实体
     *
     * @param entity 实体
     */
    @Override
    public BaseEntity selectEntity(BaseEntity entity) {
        return super.selectEntity(entity);
    }
}
