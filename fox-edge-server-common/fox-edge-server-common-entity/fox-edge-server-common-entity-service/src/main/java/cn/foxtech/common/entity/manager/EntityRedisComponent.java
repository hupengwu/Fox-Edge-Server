/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.*;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.utils.common.utils.redis.service.RedisService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.*;

/**
 * Redis部件
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class EntityRedisComponent {
    /**
     * 生产者:Entity结构的数据
     */
    private final Set<String> producer = new HashSet<>();
    /**
     * 消费者：Entity结构的数据
     */
    private final Set<String> consumer = new HashSet<>();

    private final Set<String> reader = new HashSet<>();
    private final Set<String> writer = new HashSet<>();

    private RedisService redisService;

    protected <T> T getEntity(Long id, Class<T> clazz) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) producerRedisService.getEntity(id);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) consumerRedisService.getEntity(id);
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.readEntity(id);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    protected <T> T getEntity(String entityKey, Class<T> clazz) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) producerRedisService.getEntity(entityKey);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return (T) consumerRedisService.getEntity(entityKey);
        }

        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return (T) redisReader.readEntity(entityKey);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    protected <T> BaseEntity getEntity(Class<T> clazz, IBaseFinder finder) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService.getEntity(finder);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntity(finder);
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.readEntity(finder);
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    protected <T> int getEntityCount(Class<T> clazz, IBaseFinder finder) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService.getEntityCount(finder);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityCount(finder);
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.getEntityCount(finder);
            } catch (Exception e) {
                return 0;
            }
        }

        return 0;
    }


    protected <T> T readEntity(String entityKey, Class<T> clazz) throws IOException {
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            return (T) redisReader.readEntity(entityKey);
        }

        return null;
    }

    protected <T> Map<String, Object> readHashMap(String entityKey, Class<T> clazz) {
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            return redisReader.readHashMap(entityKey);
        }

        return null;
    }

    protected void writeEntity(BaseEntity entity) {
        if (this.writer.contains(entity.getClass().getSimpleName())) {
            RedisWriter redisWriter = RedisWriterService.getInstanceBySimpleName(entity.getClass().getSimpleName(), this.redisService.getRedisTemplate());
            redisWriter.writeEntity(entity);
        }
    }

    protected <T> List<BaseEntity> getEntityList(Class<T> clazz) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService.getEntityList();
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityList();
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.readEntityList();
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        }

        return new ArrayList<>();
    }

    protected <T> List<BaseEntity> getEntityList(Class<T> clazz, IBaseFinder finder) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService.getEntityList(finder);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityList(finder);
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.readEntityList(finder);
            } catch (Exception e) {
                throw new ServiceException(e.getMessage());
            }
        }

        return new ArrayList<>();
    }

    protected <T> Map<String, BaseEntity> getEntityMap(Class<T> clazz, Collection<String> entityKeys) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService.getEntityMap(entityKeys);
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityMap(entityKeys);
        }
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            try {
                return redisReader.readEntityMap(entityKeys);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        return new HashMap<>();
    }

    protected <T> BaseRedisService getBaseRedisService(Class<T> clazz) {
        if (this.producer.contains(clazz.getSimpleName())) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return producerRedisService;
        }
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService;
        }

        return null;
    }

    protected <T> List<BaseConsumerEntityNotify> getEntityNotify(Class<T> clazz) {
        if (this.consumer.contains(clazz.getSimpleName())) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService);
            return consumerRedisService.getEntityNotify();
        }

        return null;
    }

    protected <T> BaseRedisService getBaseRedisService(String simpleName) {
        if (this.producer.contains(simpleName)) {
            ProducerRedisService producerRedisService = ProducerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            return producerRedisService;
        }
        if (this.consumer.contains(simpleName)) {
            ConsumerRedisService consumerRedisService = ConsumerRedisService.getInstanceBySimpleName(simpleName, this.redisService);
            return consumerRedisService;
        }

        return null;
    }

    protected <T> RedisReader getBaseRedisReader(Class<T> clazz) {
        if (this.reader.contains(clazz.getSimpleName())) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            return redisReader;
        }

        return null;
    }

    protected <T> RedisReader getBaseRedisReader(String entityType) {
        if (this.reader.contains(entityType)) {
            RedisReader redisReader = RedisReaderService.getInstanceBySimpleName(entityType, this.redisService.getRedisTemplate());
            return redisReader;
        }

        return null;
    }

    protected <T> RedisWriter getBaseRedisWriter(String entityType) {
        if (this.writer.contains(entityType)) {
            RedisWriter redisWriter = RedisWriterService.getInstanceBySimpleName(entityType, this.redisService.getRedisTemplate());
            return redisWriter;
        }

        return null;
    }

    protected <T> RedisWriter getBaseRedisWriter(Class<T> clazz) {
        if (this.writer.contains(clazz.getSimpleName())) {
            RedisWriter redisWriter = RedisWriterService.getInstanceBySimpleName(clazz.getSimpleName(), this.redisService.getRedisTemplate());
            return redisWriter;
        }

        return null;
    }
}
