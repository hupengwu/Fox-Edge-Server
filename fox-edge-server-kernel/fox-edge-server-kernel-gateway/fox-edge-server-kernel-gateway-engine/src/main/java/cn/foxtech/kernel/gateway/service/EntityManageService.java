package cn.foxtech.kernel.gateway.service;

import cn.foxtech.common.entity.entity.UserEntity;
import cn.foxtech.common.entity.entity.UserMenuEntity;
import cn.foxtech.common.entity.entity.UserPermissionEntity;
import cn.foxtech.common.entity.entity.UserRoleEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据实体业务
 */
@Component
public class EntityManageService extends EntityServiceManager {

    public void instance() {
        Set<String> reader = this.entityRedisComponent.getReader();

        // 注册redis读数据
        reader.add(UserEntity.class.getSimpleName());
        reader.add(UserRoleEntity.class.getSimpleName());
        reader.add(UserMenuEntity.class.getSimpleName());
        reader.add(UserPermissionEntity.class.getSimpleName());
    }
}
