package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityOptionManager;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.entity.service.channel.ChannelEntityService;
import cn.foxtech.common.entity.service.config.ConfigEntityService;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.service.devicemapping.DeviceMapperEntityService;
import cn.foxtech.common.entity.service.devicemodel.DeviceModelEntityService;
import cn.foxtech.common.entity.service.deviceobject.DeviceObjectEntityService;
import cn.foxtech.common.entity.service.devicerecord.DeviceRecordEntityService;
import cn.foxtech.common.entity.service.devicevalue.task.DeviceValueExTaskEntityService;
import cn.foxtech.common.entity.service.extendconfig.ExtendConfigEntityService;
import cn.foxtech.common.entity.service.iotdevicemodel.IotDeviceModelEntityService;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.common.entity.service.operate.OperateEntityService;
import cn.foxtech.common.entity.service.operaterecord.OperateRecordEntityService;
import cn.foxtech.common.entity.service.operatetask.channel.OperateChannelTaskEntityService;
import cn.foxtech.common.entity.service.operatetask.manual.OperateManualTaskEntityService;
import cn.foxtech.common.entity.service.operatetask.monitor.OperateMonitorTaskEntityService;
import cn.foxtech.common.entity.service.probe.ProbeEntityService;
import cn.foxtech.common.entity.service.repocomp.RepoCompEntityService;
import cn.foxtech.common.entity.service.user.UserEntityService;
import cn.foxtech.common.entity.service.usermenu.UserMenuEntityService;
import cn.foxtech.common.entity.service.userpermission.UserPermissionEntityService;
import cn.foxtech.common.entity.service.userrole.UserRoleEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * redis实体
 */
@Component
public class EntityManageService extends EntityServiceManager {
    @Autowired
    protected ChannelEntityService channelEntityService;

    @Autowired
    protected DeviceEntityService deviceEntityService;

    @Autowired
    protected DeviceObjectEntityService deviceObjectEntityService;

    @Autowired
    protected IotDeviceModelEntityService iotDeviceModelEntityService;

    @Autowired
    protected DeviceMapperEntityService deviceMapperEntityService;

    @Autowired
    protected DeviceModelEntityService deviceModelEntityService;

    @Autowired
    protected ExtendConfigEntityService extendConfigEntityService;

    @Autowired
    protected DeviceRecordEntityService deviceRecordEntityService;

    @Autowired
    protected OperateRecordEntityService operateRecordEntityService;

    @Autowired
    protected UserEntityService userEntityService;

    @Autowired
    protected UserMenuEntityService userMenuEntityService;

    @Autowired
    protected UserRoleEntityService userRoleEntityService;

    @Autowired
    protected UserPermissionEntityService userPermissionEntityService;

    @Autowired
    protected OperateEntityService operateEntityService;

    @Autowired
    protected OperateMonitorTaskEntityService operateMonitorTaskEntityService;

    @Autowired
    protected OperateManualTaskEntityService operateManualTaskEntityService;

    @Autowired
    protected OperateChannelTaskEntityService operateChannelTaskEntityService;


    @Autowired
    protected ConfigEntityService configEntityService;

    @Autowired
    protected ProbeEntityService probeEntityService;


    @Autowired
    protected DeviceValueExTaskEntityService deviceValueExTaskEntityService;

    @Autowired
    protected RepoCompEntityService repoCompEntityService;

    @Autowired
    private EntityPublishManager entityPublishManager;

    @Autowired
    private EntityOptionManager entityOptionManager;


    public void instance() {
        // 初始化状态数据的装载
        this.instanceStatus();

        // 初始化待发布到云端的数据
        this.instancePublish();

        // 初始化允许客户端对数据库直接进行option的操作
        this.instanceOption();
    }

    private void instanceStatus() {
        Map<String, BaseEntityService> dBService = this.entityMySQLComponent.getDBService();
        Set<String> producer = this.entityRedisComponent.getProducer();
        Set<String> consumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();
        Set<String> writer = this.entityRedisComponent.getWriter();


        // 注册数据库实体
        dBService.put(DeviceEntity.class.getSimpleName(), this.deviceEntityService);
        dBService.put(ChannelEntity.class.getSimpleName(), this.channelEntityService);
        dBService.put(ConfigEntity.class.getSimpleName(), this.configEntityService);
        dBService.put(UserEntity.class.getSimpleName(), this.userEntityService);
        dBService.put(UserMenuEntity.class.getSimpleName(), this.userMenuEntityService);
        dBService.put(UserRoleEntity.class.getSimpleName(), this.userRoleEntityService);
        dBService.put(UserPermissionEntity.class.getSimpleName(), this.userPermissionEntityService);
        dBService.put(DeviceObjectEntity.class.getSimpleName(), this.deviceObjectEntityService);
        dBService.put(DeviceMapperEntity.class.getSimpleName(), this.deviceMapperEntityService);
        dBService.put(DeviceModelEntity.class.getSimpleName(), this.deviceModelEntityService);
        dBService.put(ExtendConfigEntity.class.getSimpleName(), this.extendConfigEntityService);
        dBService.put(DeviceRecordEntity.class.getSimpleName(), this.deviceRecordEntityService);
        dBService.put(ProbeEntity.class.getSimpleName(), this.probeEntityService);
        dBService.put(DeviceValueExTaskEntity.class.getSimpleName(), this.deviceValueExTaskEntityService);
        dBService.put(OperateRecordEntity.class.getSimpleName(), this.operateRecordEntityService);
        dBService.put(OperateMonitorTaskEntity.class.getSimpleName(), this.operateMonitorTaskEntityService);
        dBService.put(OperateManualTaskEntity.class.getSimpleName(), this.operateManualTaskEntityService);
        dBService.put(OperateChannelTaskEntity.class.getSimpleName(), this.operateChannelTaskEntityService);
        dBService.put(OperateEntity.class.getSimpleName(), this.operateEntityService);
        dBService.put(IotDeviceModelEntity.class.getSimpleName(), this.iotDeviceModelEntityService);
        dBService.put(RepoCompEntity.class.getSimpleName(), this.repoCompEntityService);


        // 告知：生产者如何装载数据源
        this.getSourceMySQL().add(ChannelEntity.class.getSimpleName());
        this.getSourceMySQL().add(ConfigEntity.class.getSimpleName());
        this.getSourceMySQL().add(UserEntity.class.getSimpleName());
        this.getSourceMySQL().add(UserMenuEntity.class.getSimpleName());
        this.getSourceMySQL().add(UserRoleEntity.class.getSimpleName());
        this.getSourceMySQL().add(UserPermissionEntity.class.getSimpleName());
        this.getSourceMySQL().add(ExtendConfigEntity.class.getSimpleName());
        this.getSourceMySQL().add(ProbeEntity.class.getSimpleName());
        this.getSourceMySQL().add(DeviceValueExTaskEntity.class.getSimpleName());
        this.getSourceMySQL().add(OperateEntity.class.getSimpleName());
        this.getSourceMySQL().add(OperateMonitorTaskEntity.class.getSimpleName());
        this.getSourceMySQL().add(OperateManualTaskEntity.class.getSimpleName());
        this.getSourceMySQL().add(OperateChannelTaskEntity.class.getSimpleName());
        this.getSourceMySQL().add(DeviceMapperEntity.class.getSimpleName());
        this.getSourceMySQL().add(DeviceModelEntity.class.getSimpleName());
        this.getSourceMySQL().add(IotDeviceModelEntity.class.getSimpleName());
        this.getSourceMySQL().add(RepoCompEntity.class.getSimpleName());

        // 注册消费者
        consumer.add(DeviceObjInfEntity.class.getSimpleName());
        consumer.add(OperateMethodEntity.class.getSimpleName());

        // 注册redis读数据
        reader.add(ChannelStatusEntity.class.getSimpleName());
        reader.add(DeviceValueEntity.class.getSimpleName());
        reader.add(DeviceValueExEntity.class.getSimpleName());
        reader.add(DeviceStatusEntity.class.getSimpleName());

        reader.add(DeviceEntity.class.getSimpleName());
        writer.add(DeviceEntity.class.getSimpleName());
        reader.add(OperateEntity.class.getSimpleName());
        writer.add(OperateEntity.class.getSimpleName());
        reader.add(RepoCompEntity.class.getSimpleName());
        writer.add(RepoCompEntity.class.getSimpleName());
        reader.add(IotDeviceModelEntity.class.getSimpleName());
        writer.add(IotDeviceModelEntity.class.getSimpleName());
        reader.add(DeviceModelEntity.class.getSimpleName());
        writer.add(DeviceModelEntity.class.getSimpleName());
        reader.add(DeviceMapperEntity.class.getSimpleName());
        writer.add(DeviceMapperEntity.class.getSimpleName());
        reader.add(OperateMonitorTaskEntity.class.getSimpleName());
        writer.add(OperateMonitorTaskEntity.class.getSimpleName());
        reader.add(OperateManualTaskEntity.class.getSimpleName());
        writer.add(OperateManualTaskEntity.class.getSimpleName());
        reader.add(OperateChannelTaskEntity.class.getSimpleName());
        writer.add(OperateChannelTaskEntity.class.getSimpleName());
        reader.add(ChannelEntity.class.getSimpleName());
        writer.add(ChannelEntity.class.getSimpleName());
        reader.add(ConfigEntity.class.getSimpleName());
        writer.add(ConfigEntity.class.getSimpleName());
        reader.add(UserEntity.class.getSimpleName());
        writer.add(UserEntity.class.getSimpleName());
        reader.add(UserMenuEntity.class.getSimpleName());
        writer.add(UserMenuEntity.class.getSimpleName());
        reader.add(UserRoleEntity.class.getSimpleName());
        writer.add(UserRoleEntity.class.getSimpleName());
        reader.add(UserPermissionEntity.class.getSimpleName());
        writer.add(UserPermissionEntity.class.getSimpleName());
        reader.add(ExtendConfigEntity.class.getSimpleName());
        writer.add(ExtendConfigEntity.class.getSimpleName());
        reader.add(ProbeEntity.class.getSimpleName());
        writer.add(ProbeEntity.class.getSimpleName());
        reader.add(DeviceValueExTaskEntity.class.getSimpleName());
        writer.add(DeviceValueExTaskEntity.class.getSimpleName());
    }

    private void instancePublish() {
        // 数据的发布模式
        this.entityPublishManager.setPublishEntityUpdateTime(ChannelEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, ChannelEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(ConfigEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, ConfigEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(UserEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, UserEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(UserMenuEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, UserMenuEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(UserRoleEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, UserRoleEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(UserPermissionEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, UserPermissionEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, DeviceEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(ExtendConfigEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, ExtendConfigEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(ProbeEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, ProbeEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(OperateEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, OperateEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(OperateMonitorTaskEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, OperateMonitorTaskEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(OperateManualTaskEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, OperateManualTaskEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(OperateChannelTaskEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, OperateChannelTaskEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceMapperEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, DeviceMapperEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceModelEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, DeviceModelEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(IotDeviceModelEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, IotDeviceModelEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(RepoCompEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, RepoCompEntity.class.getSimpleName());
    }

    private void instanceOption() {
        this.entityOptionManager.setOptionEntity(ChannelEntity.class.getSimpleName(), "tb_channel", new String[]{"channel_name", "channel_type"});
        this.entityOptionManager.setOptionEntity(OperateEntity.class.getSimpleName(), "tb_operate", new String[]{"manufacturer", "device_type", "operate_name", "engine_type"});
        this.entityOptionManager.setOptionEntity(DeviceEntity.class.getSimpleName(), "tb_device", new String[]{"device_type", "device_name", "channel_type", "channel_name"});
    }
}
