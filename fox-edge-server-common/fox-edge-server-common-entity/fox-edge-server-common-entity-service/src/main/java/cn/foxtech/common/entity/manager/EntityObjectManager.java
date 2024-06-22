package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.common.entity.service.redis.*;
import cn.foxtech.common.entity.utils.EntityServiceUtils;
import cn.foxtech.core.exception.ServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.*;

/**
 * 对象管理器：在父类的基础上，增加对象级的操作方法
 */
public class EntityObjectManager extends EntityBaseManager {
    public <T> T getEntity(Long id, Class<T> clazz) {
        return this.entityRedisComponent.getEntity(id, clazz);
    }

    /**
     * 获得实体对象
     *
     * @param entityKey 业务key
     * @param clazz     对象类型
     * @param <T>       对象类型
     * @return 对象
     */
    public <T> T getEntity(String entityKey, Class<T> clazz) {
        return this.entityRedisComponent.getEntity(entityKey, clazz);
    }

    public <T> boolean hasEntity(String entityKey, Class<T> clazz) {
        return this.entityRedisComponent.getEntity(entityKey, clazz) != null;
    }

    public <T> boolean hasEntity(Long id, Class<T> clazz) {
        return this.entityRedisComponent.getEntity(id, clazz) != null;
    }

    /**
     * 获得实体对象
     *
     * @param entityKey 业务key
     * @param clazz     对象类型
     * @param <T>       类型
     * @return 对象
     * @throws IOException 异常信息
     */
    public <T> T readEntity(String entityKey, Class<T> clazz) throws IOException {
        return this.entityRedisComponent.readEntity(entityKey, clazz);
    }

    public <T> Map<String, BaseEntity> getEntityMap(Collection<String> entityKeys, Class<T> clazz) {
        return this.entityRedisComponent.getEntityMap(clazz, entityKeys);
    }

    public <T> Map<String, Object> readHashMap(String entityKey, Class<T> clazz) throws JsonProcessingException {
        return this.entityRedisComponent.readHashMap(entityKey, clazz);
    }

    public <T> void writeEntity(BaseEntity entity) {
        this.entityRedisComponent.writeEntity(entity);
    }

    public <T> HashMapRedisService getHashMapService(String simpleName) {
        return this.entityHashMapComponent.getBaseRedisService(simpleName);
    }

    public <T> AgileMapRedisService getAgileMapService(String simpleName) {
        return this.entityAgileMapComponent.getBaseRedisService(simpleName);
    }

    public <T> BaseRedisService getBaseRedisService(Class<T> clazz) {
        return this.entityRedisComponent.getBaseRedisService(clazz.getSimpleName());
    }

    public BaseRedisService getBaseRedisService(String entityType) {
        return this.entityRedisComponent.getBaseRedisService(entityType);
    }

    public <T> RedisReader getRedisReader(Class<T> clazz) {
        return this.entityRedisComponent.getBaseRedisReader(clazz);
    }

    public <T> RedisReader getRedisReader(String entityType) {
        return this.entityRedisComponent.getBaseRedisReader(entityType);
    }

    public <T> RedisWriter getRedisWriter(Class<T> clazz) {
        return this.entityRedisComponent.getBaseRedisWriter(clazz);
    }

    public Long removeReloadedFlag(String entityType) {
        return this.entityChangeComponent.getReloadMap().remove(entityType);
    }

    public <T> List<BaseConsumerEntityNotify> getEntityNotify(Class<T> clazz) {
        return this.entityRedisComponent.getEntityNotify(clazz);
    }

    public Set<String> getEntityTypeList() {
        Set<String> result = new HashSet<>();
        result.addAll(this.entityRedisComponent.getProducer());
        result.addAll(this.entityRedisComponent.getConsumer());
        return result;
    }

    public <T> List<BaseEntity> getEntityList(Class<T> clazz) {
        return this.entityRedisComponent.getEntityList(clazz);
    }

    public <T> List<Map<String, Object>> getHashMapList(Class<T> clazz) {
        return this.entityHashMapComponent.getEntityList(clazz);
    }

    /**
     * 插入DR和RD实体
     *
     * @param entity 实体
     */
    public void insertEntity(BaseEntity entity) {
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityService(entity.getClass());
        if (producerEntityService == null) {
            throw new ServiceException("在DB上未注册该实体：" + entity.getClass().getSimpleName());
        }

        // 生产者模式
        if (this.entityRedisComponent.getProducer().contains(entity.getClass().getSimpleName())) {
            ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
            EntityServiceUtils.insertEntity(producerEntityService, producerRedisService, entity);
            return;
        }

        // 直写redis模式
        if (this.entityRedisComponent.getWriter().contains(entity.getClass().getSimpleName())) {
            RedisWriter redisWriter = this.entityRedisComponent.getBaseRedisWriter(entity.getClass().getSimpleName());
            EntityServiceUtils.insertEntity(producerEntityService, redisWriter, entity);
            return;
        }
    }

    /**
     * 插入实体到DR
     *
     * @param entity 实体
     */
    public void insertRDEntity(BaseEntity entity) {
        // 获得Redis部件
        if (!this.entityRedisComponent.getProducer().contains(entity.getClass().getSimpleName())) {
            throw new ServiceException("在RD上未注册该实体：" + entity.getClass().getSimpleName());
        }

        // 获得数据库部件
        ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
        Long time = System.currentTimeMillis();
        entity.setCreateTime(time);
        entity.setUpdateTime(time);
        producerRedisService.insertEntity(entity);
    }


    /**
     * 删除DB和RD上的实体
     *
     * @param entity 实体
     */
    public void deleteEntity(BaseEntity entity) {
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityService(entity.getClass());
        if (producerEntityService == null) {
            throw new ServiceException("在DB上未注册该实体：" + entity.getClass().getSimpleName());
        }

        // 生产者模式
        if (this.entityRedisComponent.getProducer().contains(entity.getClass().getSimpleName())) {
            ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
            EntityServiceUtils.deleteEntity(producerEntityService, producerRedisService, entity);
            return;
        }

        // 直写redis模式
        if (this.entityRedisComponent.getWriter().contains(entity.getClass().getSimpleName())) {
            RedisWriter redisWriter = this.entityRedisComponent.getBaseRedisWriter(entity.getClass().getSimpleName());
            EntityServiceUtils.deleteEntity(producerEntityService, redisWriter, entity);
            return;
        }
    }

    /**
     * 删除DB和RD上的实体
     *
     * @param id    ID
     * @param clazz 类型
     * @param <T>   类型
     */
    public <T> void deleteEntity(Long id, Class<T> clazz) {
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityService(clazz);
        if (producerEntityService == null) {
            throw new ServiceException("在DB上未注册该实体：" + clazz.getSimpleName());
        }

        BaseEntity entity = (BaseEntity) this.getEntity(id, clazz);
        if (entity == null) {
            return;
        }

        // 生产者模式
        if (this.entityRedisComponent.getProducer().contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
            EntityServiceUtils.deleteEntity(producerEntityService, producerRedisService, entity);
            return;
        }

        // 直写redis模式
        if (this.entityRedisComponent.getWriter().contains(clazz.getSimpleName())) {
            RedisWriter redisWriter = this.entityRedisComponent.getBaseRedisWriter(clazz.getSimpleName());
            EntityServiceUtils.deleteEntity(producerEntityService, redisWriter, entity);
            return;
        }
    }

    public <T> void deleteEntity(String serviceKey, Class<T> clazz) {
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityService(clazz);
        if (producerEntityService == null) {
            throw new ServiceException("在DB上未注册该实体：" + clazz.getSimpleName());
        }

        BaseEntity entity = (BaseEntity) this.getEntity(serviceKey, clazz);
        if (entity == null) {
            return;
        }

        // 生产者模式
        if (this.entityRedisComponent.getProducer().contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
            EntityServiceUtils.deleteEntity(producerEntityService, producerRedisService, entity);
            return;
        }

        // 直写redis模式
        if (this.entityRedisComponent.getWriter().contains(clazz.getSimpleName())) {
            RedisWriter redisWriter = this.entityRedisComponent.getBaseRedisWriter(clazz.getClass().getSimpleName());
            EntityServiceUtils.deleteEntity(producerEntityService, redisWriter, entity);
            return;
        }
    }

    /**
     * 删除RD上的实体
     *
     * @param serviceKey 业务特征
     * @param clazz      类型
     * @param <T>        类型
     */
    public <T> void deleteRDEntity(String serviceKey, Class<T> clazz) {
        if (!this.entityRedisComponent.getProducer().contains(clazz.getSimpleName())) {
            throw new ServiceException("在RD上未注册该实体：" + clazz.getSimpleName());
        }

        ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(clazz);
        BaseEntity entity = producerRedisService.getEntity(serviceKey);
        if (entity == null) {
            return;
        }

        producerRedisService.deleteEntity(entity.makeServiceKey());
    }

    /**
     * 更新DB和RD上的实体
     *
     * @param entity 实体
     */
    public void updateEntity(BaseEntity entity) {
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityService(entity.getClass());
        if (producerEntityService == null) {
            throw new ServiceException("在DB上未注册该实体：" + entity.getClass().getSimpleName());
        }

        // 生产者模式
        if (this.entityRedisComponent.getProducer().contains(entity.getClass().getSimpleName())) {
            // 获得数据库部件
            ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());
            EntityServiceUtils.updateEntity(producerEntityService, producerRedisService, entity);
            return;
        }

        // 直写redis模式
        if (this.entityRedisComponent.getWriter().contains(entity.getClass().getSimpleName())) {
            // 获得数据库部件
            RedisWriter redisWriter = this.entityRedisComponent.getBaseRedisWriter(entity.getClass().getSimpleName());
            EntityServiceUtils.updateEntity(producerEntityService, redisWriter, entity);
            return;
        }
    }

    /**
     * 更新RD上的实体
     *
     * @param entity 实体
     */
    public void updateRDEntity(BaseEntity entity) {
        // 获得REDIS部件
        if (!this.entityRedisComponent.getProducer().contains(entity.getClass().getSimpleName())) {
            throw new ServiceException("在RD上未注册该实体：" + entity.getClass().getSimpleName());
        }

        // 获得数据库部件
        ProducerRedisService producerRedisService = (ProducerRedisService) this.entityRedisComponent.getBaseRedisService(entity.getClass());

        // 获得原来的对象
        BaseEntity existEntity = producerRedisService.getEntity(entity.makeServiceKey());
        if (existEntity == null) {
            return;
        }

        // 继承原来的创建时间和刷新更新时间
        entity.setCreateTime(existEntity.getCreateTime());
        entity.setUpdateTime(System.currentTimeMillis());
        producerRedisService.updateEntity(entity);
    }
}
