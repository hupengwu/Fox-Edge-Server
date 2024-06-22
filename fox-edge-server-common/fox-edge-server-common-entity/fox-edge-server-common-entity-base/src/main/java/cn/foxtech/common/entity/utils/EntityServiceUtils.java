package cn.foxtech.common.entity.utils;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.common.entity.service.redis.*;
import org.springframework.data.redis.serializer.SerializationException;

import java.util.List;

/**
 * 实体数据的管理：将自己的生产的数据发布到redis、云端，或者从redis消费别人生产的数据，并发布到云端
 */
public class EntityServiceUtils {
    /**
     * 初始化装载数据到生产者类型的redis缓存对象的缓存中，此时尚未写入redis服务器
     *
     * @param redisService
     * @param entityService
     */
    public static void initLoadEntity(BaseProducerRedisService redisService, BaseEntityService entityService) {
        try {
            // 检查：是否已经初始化过
            if (!redisService.isInited()) {
                // 从数据库装载数据
                List<BaseEntity> entityList = entityService.selectEntityList();

                // 保存到本地缓存中
                redisService.setDataMap(entityList);
                redisService.setInited();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initLoadEntity(BaseProducerRedisService redisService, BaseRedisService otherRedisService) {
        try {
            // 检查：是否已经初始化过
            if (!redisService.isInited()) {
                // 从数据库装载数据
                List<BaseEntity> entityList = otherRedisService.getEntityList();

                // 保存到本地缓存中
                redisService.setDataMap(entityList);
                redisService.setInited();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initLoadEntity(BaseProducerRedisService redisService, List<BaseEntity> initEntityList) {
        try {
            // 检查：是否已经初始化过
            if (!redisService.isInited()) {
                // 保存到本地缓存中
                redisService.setDataMap(initEntityList);
                redisService.setInited();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发布更新生产者的缓存数据，到redis服务器和云端服务器
     */
    public static boolean publishEntity(BaseProducerRedisService redisService) {
        try {
            if (redisService.isNeedSave()) {
                // 同步到redis
                redisService.saveAgileEntities();
                return true;
            }
        } catch (SerializationException se) {
            try {
                // 数据结构发生了变化，旧数据格式不匹配，需要清空缓存，等待下一次发布
                redisService.cleanAgileEntities();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 消费者，从redis服务器中
     */
    public static boolean reloadRedis(BaseConsumerRedisService redisService) {
        try {
            boolean reloaded = false;
            if (redisService.isNeedLoad()) {
                redisService.loadAgileEntities();
                reloaded = true;
            }

            redisService.setInited();
            return reloaded;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reloadRedis(HashMapRedisService redisService) {
        try {
            boolean reloaded = false;
            if (redisService.isNeedLoad()) {
                redisService.loadAgileEntities();
                reloaded = true;
            }

            redisService.setInited();
            return reloaded;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean reloadRedis(AgileMapRedisService redisService) {
        try {
            boolean reloaded = false;
            if (redisService.isNeedLoad()) {
                redisService.loadAgileEntities();
                reloaded = true;
            }

            redisService.setInited();
            return reloaded;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 插入数据
     *
     * @param entityService 数据库操作
     * @param redisService  redis操作
     * @param baseEntity    实体数据
     */
    public static void insertEntity(BaseEntityService entityService, BaseProducerRedisService redisService, BaseEntity baseEntity) {
        entityService.insertEntity(baseEntity);
        redisService.insertEntity(baseEntity);
    }

    public static void insertEntity(BaseEntityService entityService, RedisWriter redisWriter, BaseEntity baseEntity) {
        entityService.insertEntity(baseEntity);
        redisWriter.writeEntity(baseEntity);
    }

    /**
     * 删除实体
     *
     * @param entityService 数据库操作
     * @param redisService  redis操作
     * @param baseEntity    实体数据
     */
    public static void deleteEntity(BaseEntityService entityService, BaseProducerRedisService redisService, BaseEntity baseEntity) {
        entityService.deleteEntity(baseEntity);
        redisService.deleteEntity(baseEntity.makeServiceKey());
    }

    public static void deleteEntity(BaseEntityService entityService, RedisWriter redisWriter, BaseEntity baseEntity) {
        entityService.deleteEntity(baseEntity);
        redisWriter.deleteEntity(baseEntity.makeServiceKey());
    }

    /**
     * 更新
     *
     * @param entityService 数据库操作
     * @param redisService  redis操作
     * @param baseEntity    实体数据
     */
    public static void updateEntity(BaseEntityService entityService, BaseProducerRedisService redisService, BaseEntity baseEntity) {
        entityService.updateEntity(baseEntity);
        redisService.updateEntity(baseEntity);
    }

    public static void updateEntity(BaseEntityService entityService, RedisWriter redisWriter, BaseEntity baseEntity) {
        entityService.updateEntity(baseEntity);
        redisWriter.writeEntity(baseEntity);
    }
}
