package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import com.fasterxml.jackson.core.JsonParseException;

import java.util.List;

/**
 * Redis数据的生产者：
 * 1、生产者在初始化阶段，会从其他途径保存数据到本地内存，然后标识已经初始化完毕
 * 2、生产者通过增删改动作后，内部会产生已经修改过的标识
 * 3、生产者通过isNeedSave来获知数据已经修改，然后通过saveEntities把最新数据发布到redis服务
 */
public abstract class BaseProducerRedisService extends BaseRedisService {
    /**
     * 是否需要更新数据到redis
     *
     * @return
     */
    @Override
    public boolean isNeedSave() {
        return super.isNeedSave();
    }

    /**
     * 更新数据到redis
     */
    @Override
    public void saveAllEntities() {
        super.saveAllEntities();
    }

    @Override
    public void saveAgileEntities() {
        super.saveAgileEntities();
    }

    @Override
    public void cleanAgileEntities() {
        super.cleanAgileEntities();
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

    /**
     * 将整个缓存设置为新的实体列表
     *
     * @param dataMap
     */
    @Override
    public void setDataMap(List<BaseEntity> dataMap) {
        super.setDataMap(dataMap);
    }

    @Override
    public void loadAllEntities() throws JsonParseException {
        super.loadAllEntities();
    }

    /**
     * 插入实体
     *
     * @param entity 实体
     */
    @Override
    public void insertEntity(BaseEntity entity) {
        super.insertEntity(entity);
    }


    /**
     * 根据Key特征，更新实体
     *
     * @param entity 实体
     */
    @Override
    public void updateEntity(BaseEntity entity) {
        super.updateEntity(entity);
    }

    /**
     * 根据Key特征，删除实体
     *
     * @param entityKey 实体
     */
    @Override
    public void deleteEntity(String entityKey) {
        super.deleteEntity(entityKey);
    }
}
