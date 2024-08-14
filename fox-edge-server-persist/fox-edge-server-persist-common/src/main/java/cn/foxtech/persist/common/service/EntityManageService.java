/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.service;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityOptionManager;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.entity.service.devicehistory.DeviceHistoryEntityService;
import cn.foxtech.common.entity.service.deviceobject.DeviceObjectEntityService;
import cn.foxtech.common.entity.service.operaterecord.OperateRecordEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 实体管理操作：提供设备信息查询和构造服务
 */
@Getter(value = AccessLevel.PUBLIC)
@Component
public class EntityManageService extends EntityServiceManager {
    /**
     * 历史记录
     */
    @Autowired
    protected DeviceHistoryEntityService deviceHistoryEntityService;
    /**
     * 发布注册
     */
    @Autowired
    protected EntityPublishManager entityPublishManager;

    @Autowired
    private EntityOptionManager entityOptionManager;

    /**
     * 操作记录
     */
    @Autowired
    private OperateRecordEntityService operateRecordEntityService;
    /**
     * 设备对象记录
     */
    @Autowired
    private DeviceObjectEntityService deviceObjectEntityService;

    public void initialize() {
        // 初始化状态数据的装载
        this.instanceStatus();

        // 初始化待发布到云端的数据
        this.instancePublish();

        // 初始化允许客户端对数据库直接进行option的操作
        this.instanceOption();
    }

    private void instanceStatus() {
        Set<String> producer = this.entityRedisComponent.getProducer();
        Set<String> consumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();
        Set<String> writer = this.entityRedisComponent.getWriter();
        Set<String> sourceRedis = this.getSourceRedis();


        // 注册初始化数据的数据源：从redis中继承上次的数据
        sourceRedis.add(DeviceObjInfEntity.class.getSimpleName());

        // 注册生产者:数据量小，高频操作，所以采用缓存方式
        producer.add(DeviceObjInfEntity.class.getSimpleName());

        // 注册redis读缓存数据
        consumer.add(ConfigEntity.class.getSimpleName());
        consumer.add(DeviceMapperEntity.class.getSimpleName());

        // 注册redis直接读写数据
        reader.add(DeviceEntity.class.getSimpleName());
        reader.add(DeviceValueEntity.class.getSimpleName());
        writer.add(DeviceValueEntity.class.getSimpleName());
        reader.add(DeviceStatusEntity.class.getSimpleName());
        writer.add(DeviceStatusEntity.class.getSimpleName());
        reader.add(DeviceObjInfEntity.class.getSimpleName());
        writer.add(DeviceObjInfEntity.class.getSimpleName());
    }

    private void instancePublish() {
        // 数据的发布模式
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceValueEntity.class.getSimpleName(), EntityPublishConstant.value_mode_value, EntityPublishConstant.value_type_redis, DeviceValueEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceStatusEntity.class.getSimpleName(), EntityPublishConstant.value_mode_value, EntityPublishConstant.value_type_redis, DeviceStatusEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceObjectEntity.class.getSimpleName(), EntityPublishConstant.value_mode_define, EntityPublishConstant.value_type_mysql, "tb_device_object");
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceHistoryEntity.class.getSimpleName(), EntityPublishConstant.value_mode_logger, EntityPublishConstant.value_type_mysql, "tb_device_history");
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceRecordEntity.class.getSimpleName(), EntityPublishConstant.value_mode_record, EntityPublishConstant.value_type_mysql, "tb_device_record");
        this.entityPublishManager.setPublishEntityUpdateTime(OperateRecordEntity.class.getSimpleName(), EntityPublishConstant.value_mode_record, EntityPublishConstant.value_type_mysql, "tb_operate_record");
    }

    private void instanceOption() {
        this.entityOptionManager.setOptionEntity(DeviceObjectEntity.class.getSimpleName(), "tb_device_object", new String[]{"device_name", "device_type", "object_name"});
    }
}
