package cn.foxtech.trigger.service.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.entity.service.triggerobject.TriggerObjectEntityService;
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
    private TriggerObjectEntityService triggerObjectEntityService;

    @Autowired
    private EntityPublishManager entityPublishManager;

    public void instance() {
        Set<String> producer = this.entityRedisComponent.getProducer();
        Set<String> consumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();
        Set<String> writer = this.entityRedisComponent.getWriter();

        // 告知：生产者如何装载数据源
        this.getSourceRedis().add(TriggerMethodEntity.class.getSimpleName());

        producer.add(TriggerMethodEntity.class.getSimpleName());

        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(TriggerEntity.class.getSimpleName());
        consumer.add(TriggerConfigEntity.class.getSimpleName());

        // 注册redis直接读写数据
        reader.add(DeviceValueEntity.class.getSimpleName());
        reader.add(TriggerValueEntity.class.getSimpleName());
        writer.add(TriggerValueEntity.class.getSimpleName());

        // 数据的发布模式
        this.entityPublishManager.setPublishEntityUpdateTime(TriggerMethodEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, TriggerMethodEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(TriggerValueEntity.class.getSimpleName(), EntityPublishConstant.value_mode_value, EntityPublishConstant.value_type_redis, TriggerValueEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(TriggerObjectEntity.class.getSimpleName(), EntityPublishConstant.value_mode_define, EntityPublishConstant.value_type_mysql, "tb_trigger_object");
    }
}
