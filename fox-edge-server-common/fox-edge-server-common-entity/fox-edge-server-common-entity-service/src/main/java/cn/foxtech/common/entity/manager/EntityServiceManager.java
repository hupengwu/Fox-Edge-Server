package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.service.redis.BaseRedisService;
import cn.foxtech.common.entity.service.redis.IBaseFinder;
import cn.foxtech.common.entity.service.usermenu.UserMenuEntityService;
import cn.foxtech.common.entity.service.usermenu.UserMenuMaker;
import cn.foxtech.common.entity.service.userpermission.UserPermissionEntityService;
import cn.foxtech.common.entity.service.userpermission.UserPermissionMaker;
import cn.foxtech.common.entity.service.userrole.UserRoleEntityService;
import cn.foxtech.common.entity.service.userrole.UserRoleMaker;
import cn.foxtech.common.utils.ContainerUtils;

import java.util.ArrayList;
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

    public LinkEntity getLinkEntity(Long id) {
        return super.getEntity(id, LinkEntity.class);
    }

    public List<BaseEntity> getChannelEntity(String channelType) {
        return this.getEntityList(ChannelEntity.class, (Object value) -> {
            ChannelEntity entity = (ChannelEntity) value;

            boolean result = true;

            result &= entity.getChannelType().equals(channelType);

            return result;
        });
    }

    public List<BaseEntity> getLinkEntity(String linkType) {
        return this.getEntityList(LinkEntity.class, (Object value) -> {
            LinkEntity entity = (LinkEntity) value;

            boolean result = true;

            result &= entity.getLinkType().equals(linkType);

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

    public OperateMonitorTaskEntity getOperateMonitorTaskEntity(String templateName) {
        OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
        entity.setTemplateName(templateName);
        return super.getEntity(entity.makeServiceKey(), OperateMonitorTaskEntity.class);
    }

    public OperateManualTaskEntity getOperateManualTaskEntity(String taskName) {
        OperateManualTaskEntity entity = new OperateManualTaskEntity();
        entity.setTaskName(taskName);
        return super.getEntity(entity.makeServiceKey(), OperateManualTaskEntity.class);
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


    public ProbeEntity getProbeEntity(Long id) {
        return super.getEntity(id, ProbeEntity.class);
    }


    public List<BaseEntity> getProbeEntityList() {
        return super.getEntityList(ProbeEntity.class);
    }


    public List<DeviceEntity> getDeviceEntityList(String channelType, String channelName) {
        List<DeviceEntity> resultList = new ArrayList<>();

        List<BaseEntity> entityList = super.getEntityList(DeviceEntity.class);
        for (BaseEntity baseEntity : entityList) {
            DeviceEntity entity = (DeviceEntity) baseEntity;
            if (!entity.getChannelName().equals(channelName)) {
                continue;
            }
            if (!entity.getChannelType().equals(channelType)) {
                continue;
            }

            resultList.add(entity);
        }

        return resultList;
    }

    public <T> void foreachFinder(Class<T> clazz, IBaseFinder finder) {
        BaseRedisService entityRedisService = super.getBaseRedisService(clazz);
        entityRedisService.foreachFinder(finder);
    }

    public <T> List<BaseEntity> getEntityList(Class<T> clazz, IBaseFinder finder) {
        BaseRedisService entityRedisService = super.getBaseRedisService(clazz);
        return entityRedisService.getEntityList(finder);
    }

    public <T> T getEntity(Class<T> clazz, IBaseFinder finder) {
        BaseRedisService entityRedisService = super.getBaseRedisService(clazz);
        return (T) entityRedisService.getEntity(finder);
    }

    public <T> int getEntityCount(Class<T> clazz, IBaseFinder finder) {
        BaseRedisService entityRedisService = super.getBaseRedisService(clazz);
        return entityRedisService.getEntityCount(finder);
    }
}
