package cn.foxtech.value.ex.task.service.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据实体业务
 */
@Data
@Component
public class EntityManageService extends EntityServiceManager {
    @Autowired
    private EntityPublishManager entityPublishManager;

    public void instance() {
        Set<String> agileConsumer = this.entityAgileMapComponent.getConsumer();
        Set<String> redisConsumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();
        Set<String> writer = this.entityRedisComponent.getWriter();

        // 缓存时间戳数据
        agileConsumer.add(DeviceValueEntity.class.getSimpleName());

        // 消费者：缓存数据到本地（数据量小）
        redisConsumer.add(ConfigEntity.class.getSimpleName());
        redisConsumer.add(DeviceMapperEntity.class.getSimpleName());
        redisConsumer.add(DeviceEntity.class.getSimpleName());
        redisConsumer.add(DeviceValueExTaskEntity.class.getSimpleName());

        // 注册redis直接读写数据
        reader.add(DeviceValueExCacheEntity.class.getSimpleName());
        writer.add(DeviceValueExCacheEntity.class.getSimpleName());
        reader.add(DeviceValueExEntity.class.getSimpleName());
        writer.add(DeviceValueExEntity.class.getSimpleName());
    }
}
