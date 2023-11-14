package cn.foxtech.channel.common.service;

import cn.foxtech.common.entity.manager.EntityServiceManager;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据实体业务
 */
@Component
public class EntityManageService extends EntityServiceManager {
    public void instance(Set<String> consumer) {
        this.addConsumer(consumer);
    }
}
