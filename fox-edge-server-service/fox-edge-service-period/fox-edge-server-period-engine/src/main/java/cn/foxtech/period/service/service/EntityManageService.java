package cn.foxtech.period.service.service;

import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.manager.EntityOptionManager;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import cn.foxtech.period.service.entity.PeriodRecordEntity;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.mapper.periodtask.PeriodTaskEntityService;
import cn.foxtech.service.common.service.ServiceEntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 实体管理操作：提供设备信息查询和构造服务
 */
@Component
public class EntityManageService extends ServiceEntityManageService {
    @Autowired
    protected PeriodTaskEntityService periodTaskEntityService;

    @Autowired
    protected EntityPublishManager entityPublishManager;


    @Autowired
    protected EntityOptionManager entityOptionManager;


    public void instance() {
        Map<String, BaseEntityService> dBService = this.entityMySQLComponent.getDBService();
        Set<String> producer = this.entityRedisComponent.getProducer();
        Set<String> consumer = this.entityRedisComponent.getConsumer();
        Set<String> reader = this.entityRedisComponent.getReader();

        // 注册数据库
        dBService.put(PeriodTaskEntity.class.getSimpleName(), this.periodTaskEntityService);

        // 注册生产者
        producer.add(PeriodTaskEntity.class.getSimpleName());

        // 告知：生产者如何装载数据源
        this.getSourceMySQL().add(PeriodTaskEntity.class.getSimpleName());

        // 需要缓存到内存的实体：自动从redis同步到本地缓存，以空间换时间
        consumer.add(DeviceEntity.class.getSimpleName());

        // 直接到redis读取的实体：不自动从redis同步到缓存，以时间换空间
        reader.add(DeviceValueEntity.class.getSimpleName());

        // 数据的发布模式
        this.entityPublishManager.setPublishEntityUpdateTime(PeriodTaskEntity.class.getSimpleName(), EntityPublishConstant.value_mode_config, EntityPublishConstant.value_type_cache, PeriodTaskEntity.class.getSimpleName());
        this.entityPublishManager.setPublishEntityUpdateTime(PeriodRecordEntity.class.getSimpleName(), EntityPublishConstant.value_mode_record, EntityPublishConstant.value_type_mysql, "tb_period_record");

        // 指明允许借manage服务的option，对数据库表进行option的查询操作
        this.entityOptionManager.setOptionEntity(PeriodTaskEntity.class.getSimpleName(), "tb_period_task", new String[]{"task_name", "device_type"});
        this.entityOptionManager.setOptionEntity(PeriodRecordEntity.class.getSimpleName(), "tb_period_record", new String[]{"task_id", "record_batch", "device_id", "object_name"});
    }
}
