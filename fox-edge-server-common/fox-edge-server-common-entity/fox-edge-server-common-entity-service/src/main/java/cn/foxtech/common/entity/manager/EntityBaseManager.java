/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.common.entity.service.redis.AgileMapRedisService;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import cn.foxtech.common.entity.service.redis.HashMapRedisService;
import cn.foxtech.common.entity.service.redis.ProducerRedisService;
import cn.foxtech.common.entity.utils.EntityServiceUtils;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import com.fasterxml.jackson.core.JsonParseException;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.HashSet;
import java.util.Set;

/**
 * 基本的实体管理者
 */
public abstract class EntityBaseManager {
    private static final Logger logger = Logger.getLogger(EntityBaseManager.class);

    /**
     * 初始化生产者的数据源：从数据库装载数据，并初始化redis和缓存
     */
    @Getter(value = AccessLevel.PUBLIC)
    private final Set<String> sourceMySQL = new HashSet<>();
    /**
     * 初始化生产者的数据源：从redis装载旧数据，并初始化缓存
     */
    @Getter(value = AccessLevel.PUBLIC)
    private final Set<String> sourceRedis = new HashSet<>();
    /**
     * redis缓存组件
     */
    protected EntityRedisComponent entityRedisComponent = new EntityRedisComponent();
    /**
     * 数据库组件
     */
    protected EntityMySqlComponent entityMySQLComponent = new EntityMySqlComponent();
    /**
     * 数据变化感知组件
     */
    protected EntityChangeComponent entityChangeComponent = new EntityChangeComponent();
    /**
     * HashMap版本的redis缓存组件：只读
     */
    protected EntityHashMapComponent entityHashMapComponent = new EntityHashMapComponent();
    /**
     * 敏捷状态组件
     */
    protected EntityAgileMapComponent entityAgileMapComponent = new EntityAgileMapComponent();

    /**
     * redis部件
     */
    private RedisService redisService;

    /**
     * 初始化状态
     */
    @Getter(value = AccessLevel.PUBLIC)
    private boolean isInitialized = false;

    /**
     * 绑定
     *
     * @param redisService
     */
    protected void instance(RedisService redisService) {
        this.redisService = redisService;

        this.entityRedisComponent.setRedisService(redisService);
        this.entityHashMapComponent.setRedisService(redisService);
        this.entityAgileMapComponent.setRedisService(redisService);
    }

    public void addReader(String entityType) {
        this.entityRedisComponent.getReader().add(entityType);
    }

    public void addReader(Set<String> entityTypes) {
        this.entityRedisComponent.getReader().addAll(entityTypes);
    }

    public void addConsumer(String entityType) {
        this.entityRedisComponent.getConsumer().add(entityType);
    }

    public void addConsumer(Set<String> entityTypes) {
        this.entityRedisComponent.getConsumer().addAll(entityTypes);
    }

    public void addProducer(String entityType) {
        this.entityRedisComponent.getProducer().add(entityType);
    }

    public void addAgileConsumer(Set<String> entityTypes) {
        this.entityAgileMapComponent.getConsumer().addAll(entityTypes);
    }

    public void addAgileConsumer(String entityType) {
        this.entityAgileMapComponent.getConsumer().add(entityType);
    }


    private boolean initLoadProducerEntity(String simpleName) {
        // 如果不需要这个生产者，按成功处理，直接返回成功
        if (!this.entityRedisComponent.getProducer().contains(simpleName)) {
            return true;
        }

        // 获得RDService和DBService
        ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityServiceBySimpleName(simpleName);

        // 使用DBService初始化RDService
        if (!producerRedisService.isInited()) {
            EntityServiceUtils.initLoadEntity(producerRedisService, producerEntityService);
        }

        return producerRedisService.isInited();
    }

    private boolean initLoadProducerWriter(String simpleName) {
        // 如果不需要这个写入者，按成功处理，直接返回成功
        if (!this.entityRedisComponent.getWriter().contains(simpleName)) {
            return true;
        }

        // 如果是单纯的数据只存在于redis的实体，那直接返回成功，比如持久化服务的DeviceValueEntity、DeviceStatusEntity
        if (this.entityMySQLComponent.getEntityServiceBySimpleName(simpleName) == null) {
            return true;
        }

        // 如果同时拥有mysql和redis的生产者角色，但是为了节省内存，redis和mysql各自进行独立操作，
        // 那么在启动阶段，进行下面的将mysql数据同步给redis的操作。例如管理服务的DeviceEntity

        // 临时性的获得RDService和DBService
        ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
        BaseEntityService producerEntityService = this.entityMySQLComponent.getEntityServiceBySimpleName(simpleName);

        // 使用DBService初始化RDService
        if (!producerRedisService.isInited()) {
            EntityServiceUtils.initLoadEntity(producerRedisService, producerEntityService);
        }

        // 将数据发布到redis
        EntityServiceUtils.publishEntity(producerRedisService);


        // 然后释放掉这个临时性的producerRedisService
        ProducerRedisService.removeInstanceBySimpleName(producerRedisService);

        return true;
    }

    /**
     * 初始化生产者
     *
     * @param mysql 从数据库中装载数据，并初始化redis和缓存
     * @param redis 从redis中装载数据，并初始化缓存
     * @return 是否初始化成功
     */
    private boolean initLoadProducerEntity(Set<String> mysql, Set<String> redis) {
        boolean isInitialized = true;
        String simpleName = "";

        try {
            for (String name : mysql) {
                simpleName = name;
                isInitialized = isInitialized && this.initLoadProducerEntity(name);
            }

            for (String name : redis) {
                simpleName = name;
                isInitialized = isInitialized && this.initLoadDynamicEntity(simpleName);
            }

        } catch (JsonParseException | SerializationException e) {
            // 数据结构调整，清空缓存数据
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            producerRedisService.cleanAgileEntities();
            return false;
        }

        return isInitialized;
    }

    /**
     * 對reader/writer操作生产者进行初始化
     *
     * @param writer
     * @return
     */
    private boolean initLoadProducerWriter(Set<String> writer) {
        boolean isInitialized = true;
        String simpleName = "";

        try {
            for (String name : writer) {
                simpleName = name;
                isInitialized = isInitialized && this.initLoadProducerWriter(name);
            }

        } catch (SerializationException e) {
            // 数据结构调整，清空缓存数据
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            producerRedisService.cleanAgileEntities();
            return false;
        }

        return isInitialized;
    }

    /**
     * 初始化装载动态类型的生产者
     * 它们的数据是运行期内，由生产者进行增删改查的，在初始化阶段只是从redis中重新装载上一轮的数据
     *
     * @param simpleName
     * @param <T>
     * @return
     */
    private <T> boolean initLoadDynamicEntity(String simpleName) throws JsonParseException {
        if (!this.entityRedisComponent.getProducer().contains(simpleName)) {
            return true;
        }

        // 取出对应的生产者RedisService
        ProducerRedisService producerRedisService = ProducerRedisService.getInstance(simpleName, this.redisService);

        // 从redis中装载旧的数据
        producerRedisService.loadAllEntities();
        producerRedisService.setInited();

        return producerRedisService.isInited();
    }

    private <T> boolean initLoadConsumerEntity(String clazzSimpleName) {
        if (this.entityRedisComponent.getConsumer().contains(clazzSimpleName)) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazzSimpleName, redisService);
            if (!consumerRedisService.isInited()) {
                EntityServiceUtils.reloadRedis(consumerRedisService);
            }
            return consumerRedisService.isInited();
        }

        return false;
    }

    private <T> boolean initLoadConsumerHashMap(String clazzSimpleName) {
        if (this.entityHashMapComponent.getConsumer().contains(clazzSimpleName)) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazzSimpleName, redisService);
            if (!consumerRedisService.isInited()) {
                EntityServiceUtils.reloadRedis(consumerRedisService);
            }
            return consumerRedisService.isInited();
        }

        return false;
    }

    private <T> boolean initLoadConsumerAgileMap(String clazzSimpleName) {
        if (this.entityAgileMapComponent.getConsumer().contains(clazzSimpleName)) {
            AgileMapRedisService consumerRedisService = AgileMapRedisService.getInstanceBySimpleName(clazzSimpleName, redisService);
            if (!consumerRedisService.isInited()) {
                EntityServiceUtils.reloadRedis(consumerRedisService);
            }
            return consumerRedisService.isInited();
        }

        return false;
    }

    private boolean initLoadConsumerEntity() {
        boolean isInitialized = true;

        for (String clazzSimpleName : this.entityRedisComponent.getConsumer()) {
            isInitialized = isInitialized && this.initLoadConsumerEntity(clazzSimpleName);
        }

        return isInitialized;
    }

    private boolean initLoadConsumerHashMap() {
        boolean isInitialized = true;

        for (String clazzSimpleName : this.entityHashMapComponent.getConsumer()) {
            isInitialized = isInitialized && this.initLoadConsumerHashMap(clazzSimpleName);
        }

        return isInitialized;
    }

    private boolean initLoadConsumerAgileMap() {
        boolean isInitialized = true;

        for (String clazzSimpleName : this.entityAgileMapComponent.getConsumer()) {
            isInitialized = isInitialized && this.initLoadConsumerAgileMap(clazzSimpleName);
        }

        return isInitialized;
    }

    /**
     * 初始化装载实体
     */
    public void initLoadEntity() {
        logger.info("------------------initLoadEntity开始！------------------");
        while (!this.isInitialized) {
            try {
                Thread.sleep(1000);

                // 没有初始化成功，那么等一会，再来一次上述操作
                boolean isInitialized = true;

                // 装载生产者
                isInitialized = isInitialized && initLoadProducerEntity(this.sourceMySQL, this.sourceRedis);
                // 装载生产者
                isInitialized = isInitialized && initLoadProducerWriter(this.entityRedisComponent.getWriter());
                // 装载消费者Entity
                isInitialized = isInitialized && initLoadConsumerEntity();
                // 装载消费者HashMap
                isInitialized = isInitialized && initLoadConsumerHashMap();
                // 装载消费者AgileMap
                isInitialized = isInitialized && initLoadConsumerAgileMap();
                if (!isInitialized) {
                    Thread.sleep(3000);
                } else {
                    this.isInitialized = true;
                }
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        }
        logger.info("------------------initLoadEntity结束！------------------");
    }

    /**
     * 同步实体到redis
     */
    public void syncEntity() {
        // 检查：是否已经初始化完成
        if (!this.isInitialized) {
            return;
        }

        Set<String> types = new HashSet<>();
        types.addAll(this.entityRedisComponent.getProducer());
        types.addAll(this.entityRedisComponent.getConsumer());
        types.addAll(this.entityHashMapComponent.getConsumer());

        for (String clazzSimpleName : types) {
            this.syncEntity(clazzSimpleName);
        }
    }

    public void syncEntity(String clazzSimpleName) {
        // 检查：是否已经初始化完成
        if (!this.isInitialized) {
            return;
        }

        if (this.entityRedisComponent.getProducer().contains(clazzSimpleName)) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazzSimpleName, this.redisService);
            if (EntityServiceUtils.publishEntity(producerRedisService)) {
                this.entityChangeComponent.getPublishMap().put(clazzSimpleName, producerRedisService.getUpdateTime());
            }
        }

        if (this.entityRedisComponent.getConsumer().contains(clazzSimpleName)) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazzSimpleName, redisService);
            if (EntityServiceUtils.reloadRedis(consumerRedisService)) {
                this.entityChangeComponent.getReloadMap().put(clazzSimpleName, consumerRedisService.getUpdateTime());
            }
        }

        if (this.entityHashMapComponent.getConsumer().contains(clazzSimpleName)) {
            HashMapRedisService consumerRedisService = HashMapRedisService.getInstanceBySimpleName(clazzSimpleName, redisService);
            EntityServiceUtils.reloadRedis(consumerRedisService);
        }
    }
}
