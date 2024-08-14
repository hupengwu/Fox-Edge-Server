/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.service.redis.IBaseFinder;
import cn.foxtech.common.entity.service.usermenu.UserMenuEntityService;
import cn.foxtech.common.entity.service.usermenu.UserMenuMaker;
import cn.foxtech.common.entity.service.userpermission.UserPermissionEntityService;
import cn.foxtech.common.entity.service.userpermission.UserPermissionMaker;
import cn.foxtech.common.entity.service.userrole.UserRoleEntityService;
import cn.foxtech.common.entity.service.userrole.UserRoleMaker;
import cn.foxtech.common.utils.ContainerUtils;

import java.util.List;
import java.util.Map;

public abstract class EntityServiceManager extends EntityObjectManager {
    public ChannelEntity getChannelEntity(String channelName, String channelType) {
        ChannelEntity entity = new ChannelEntity();
        entity.setChannelName(channelName);
        entity.setChannelType(channelType);
        return super.getEntity(entity.makeServiceKey(), ChannelEntity.class);
    }

    public ChannelEntity getChannelEntity(Long id) {
        return super.getEntity(id, ChannelEntity.class);
    }

    public List<BaseEntity> getChannelEntity(String channelType) {
        return this.getEntityList(ChannelEntity.class, (Object value) -> {
            ChannelEntity entity = (ChannelEntity) value;

            boolean result = true;

            result &= entity.getChannelType().equals(channelType);

            return result;
        });
    }

    public ConfigEntity getConfigEntity(String serviceName, String serviceType, String configName) {
        ConfigEntity entity = new ConfigEntity();
        entity.setServiceName(serviceName);
        entity.setServiceType(serviceType);
        entity.setConfigName(configName);
        return super.getEntity(entity.makeServiceKey(), ConfigEntity.class);
    }

    public DeviceEntity getDeviceEntity(String deviceName) {
        DeviceEntity entity = new DeviceEntity();
        entity.setDeviceName(deviceName);
        return super.getEntity(entity.makeServiceKey(), DeviceEntity.class);
    }

    public List<BaseEntity> selectUserMenuEntityListByPage(Map<String, Object> body) {
        UserMenuEntityService entityService = (UserMenuEntityService) this.entityMySQLComponent.getEntityService(UserMenuEntity.class);

        List<UserMenuPo> poList = entityService.selectEntityListByPage(body);
        List<BaseEntity> poEntityList = ContainerUtils.buildClassList(poList, BaseEntity.class);
        return UserMenuMaker.makePoList2EntityList(poEntityList);
    }

    public List<BaseEntity> selectUserRoleEntityListByPage(Map<String, Object> body) {
        UserRoleEntityService entityService = (UserRoleEntityService) this.entityMySQLComponent.getEntityService(UserRoleEntity.class);

        List<UserRolePo> poList = entityService.selectEntityListByPage(body);
        List<BaseEntity> poEntityList = ContainerUtils.buildClassList(poList, BaseEntity.class);
        return UserRoleMaker.makePoList2EntityList(poEntityList);
    }

    public List<BaseEntity> selectUserPermissionEntityListByPage(Map<String, Object> body) {
        UserPermissionEntityService entityService = (UserPermissionEntityService) this.entityMySQLComponent.getEntityService(UserPermissionEntity.class);

        List<UserPermissionPo> poList = entityService.selectEntityListByPage(body);
        List<BaseEntity> poEntityList = ContainerUtils.buildClassList(poList, BaseEntity.class);
        return UserPermissionMaker.makePoList2EntityList(poEntityList);
    }

    public UserMenuEntity getUserMenuEntity(String name) {
        UserMenuEntity entity = new UserMenuEntity();
        entity.setName(name);
        return super.getEntity(entity.makeServiceKey(), UserMenuEntity.class);
    }

    public UserRoleEntity getUserRoleEntity(String name) {
        UserRoleEntity entity = new UserRoleEntity();
        entity.setName(name);
        return super.getEntity(entity.makeServiceKey(), UserRoleEntity.class);
    }

    public UserPermissionEntity getUserPermissionEntity(String name) {
        UserPermissionEntity entity = new UserPermissionEntity();
        entity.setName(name);
        return super.getEntity(entity.makeServiceKey(), UserPermissionEntity.class);
    }

    public UserEntity getUserEntity(String username) {
        UserEntity entity = new UserEntity();
        entity.setUsername(username);
        return super.getEntity(entity.makeServiceKey(), UserEntity.class);
    }

    public <T> List<BaseEntity> getEntityList(Class<T> clazz, IBaseFinder finder) {
        return this.entityRedisComponent.getEntityList(clazz, finder);
    }

    public <T> T getEntity(Class<T> clazz, IBaseFinder finder) {
        return (T) this.entityRedisComponent.getEntity(clazz, finder);
    }

    public <T> int getEntityCount(Class<T> clazz, IBaseFinder finder) {
        return this.entityRedisComponent.getEntityCount(clazz, finder);
    }
}
