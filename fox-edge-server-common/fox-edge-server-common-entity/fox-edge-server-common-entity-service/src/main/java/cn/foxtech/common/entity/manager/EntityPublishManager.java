package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.redis.status.RedisStatusConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 发布配置管理
 * 很多服务需要通知ProxyCloud服务，把自己的数据发布到Fox-Cloud侧，此时可以通过该模块将自己的信息
 * 通知到ProxyCloud服务。
 * 该模块会将数据写入指定的redis缓存中，ProxyCloud会到指定的redis缓冲去读取这些配置数据
 */
@Component
public class EntityPublishManager {
    @Autowired
    private ServiceStatus serviceStatus;

    public void setPublishEntityUpdateTime(String entityType, String publishMode, String sourceType, String sourceName) {
        Map<String, Object> publishEntity = (Map<String, Object>) this.serviceStatus.getProducerData().computeIfAbsent(RedisStatusConstant.field_publish_entity, k -> new HashMap<>());
        Map<String, Object> entity = (Map<String, Object>) publishEntity.computeIfAbsent(entityType, k -> new HashMap<>());
        entity.put(EntityPublishConstant.field_publish_mode, publishMode);
        entity.put(EntityPublishConstant.field_source_type, sourceType);
        entity.put(EntityPublishConstant.field_source_name, sourceName);
    }

    public void setPublishEntityUpdateTime(String entityType, Long updateTime) {
        Map<String, Object> publishEntity = (Map<String, Object>) this.serviceStatus.getProducerData().computeIfAbsent(RedisStatusConstant.field_publish_entity, k -> new HashMap<>());
        Map<String, Object> entity = (Map<String, Object>) publishEntity.computeIfAbsent(entityType, k -> new HashMap<>());
        entity.put(EntityPublishConstant.field_update_time, updateTime);
    }

    public Object getPublishEntityUpdateTime(String entityType) {
        Map<String, Object> entity = this.getPublishEntity(entityType);
        if (entity == null) {
            return null;
        }

        return entity.get(EntityPublishConstant.field_update_time);
    }

    public Map<String, Object> getPublishEntity(String entityType) {
        for (Object statusValue : this.serviceStatus.getConsumerData().values()) {
            RedisStatusConsumerService.Status status = (RedisStatusConsumerService.Status) statusValue;
            Map<String, Object> value = (Map<String, Object>) status.getData();
            if (value == null) {
                continue;
            }

            Map<String, Object> publishEntity = (Map<String, Object>) value.get(RedisStatusConstant.field_publish_entity);
            if (publishEntity == null) {
                continue;
            }
            Map<String, Object> entity = (Map<String, Object>) publishEntity.get(entityType);
            if (entity == null) {
                continue;
            }

            return entity;
        }

        return null;
    }
}
