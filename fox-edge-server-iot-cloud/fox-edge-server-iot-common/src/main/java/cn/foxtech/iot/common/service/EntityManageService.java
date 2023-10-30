package cn.foxtech.iot.common.service;

import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis实体管理器：通过它，可以对Redis实体进行读写操作
 */
@Component
public class EntityManageService extends EntityServiceManager {
    public void instance(Set<String> consumer,Set<String> reader) {
        this.addConsumer(consumer);
        this.addReader(reader);
    }
}
