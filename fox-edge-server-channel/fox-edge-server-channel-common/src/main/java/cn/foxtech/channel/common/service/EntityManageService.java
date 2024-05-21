package cn.foxtech.channel.common.service;

import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.ChannelStatusEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据实体业务
 */
@Component
public class EntityManageService extends EntityServiceManager {
    public void instance(Set<String> others) {
        this.addConsumer(ConfigEntity.class.getSimpleName());
        this.addConsumer(ChannelEntity.class.getSimpleName());
        this.addConsumer(others);

        this.addProducer(ChannelStatusEntity.class.getSimpleName());

        this.getSourceRedis().add(ChannelStatusEntity.class.getSimpleName());
    }
}
