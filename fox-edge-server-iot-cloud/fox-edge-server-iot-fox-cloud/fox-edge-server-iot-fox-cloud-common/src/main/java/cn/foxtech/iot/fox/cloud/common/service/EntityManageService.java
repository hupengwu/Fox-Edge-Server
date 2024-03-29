package cn.foxtech.iot.fox.cloud.common.service;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.constant.EntityPublishConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.method.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis实体管理器：通过它，可以对Redis实体进行读写操作
 */
@Component
public class EntityManageService extends EntityServiceManager {
    @Autowired
    private ServiceStatus serviceStatus;

    public void instance() {
        // 注册消费者：里面的ConfigEntity，这是proxy-cloud自己需要用到的消费者
        this.instanceConsumer();

        // 注册订阅：这些才是发布者们委托发布的数据，里面的那个ConfigEntity跟上面的那个ConfigEntity，用途是两回事
        this.instancePublishConfig();
        this.instancePublishValue();
    }

    private void instanceConsumer() {
        Set<String> consumer = this.entityRedisComponent.getConsumer();
        // 注册消费者
        consumer.add(ConfigEntity.class.getSimpleName());
    }


    private void instancePublishConfig() {
        Set<String> statusTypeList = this.getPublishEntityTypeList(EntityPublishConstant.value_mode_config);

        this.entityAgileMapComponent.getConsumer().addAll(statusTypeList);
        this.entityHashMapComponent.getConsumer().addAll(statusTypeList);
    }

    private void instancePublishValue() {
        Set<String> reader = this.entityRedisComponent.getReader();
        Set<String> valueTypeList = this.getPublishEntityTypeList(EntityPublishConstant.value_mode_value);
        reader.addAll(valueTypeList);
    }


    /**
     * 获得要发布的订阅
     *
     * @return 实体列表
     */
    public Set<String> getPublishEntityTypeList(String mode) {
        return this.getPublishEntityMap(mode).keySet();
    }

    public Map<String, Map<String, Object>> getPublishEntityMap(String mode) {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();

        List<Map<String, Object>> dataList = this.serviceStatus.getDataList(1 * 3600 * 1000);
        for (Map<String, Object> data : dataList) {
            Map<String, Object> publishEntityMap = (Map<String, Object>) data.get(RedisStatusConstant.field_publish_entity);
            if (publishEntityMap == null) {
                continue;
            }

            for (String entityType : publishEntityMap.keySet()) {
                Map<String, Object> publishEntity = (Map<String, Object>) publishEntityMap.get(entityType);
                String sourceType = (String) publishEntity.get(EntityPublishConstant.field_source_type);
                String publishMode = (String) publishEntity.get(EntityPublishConstant.field_publish_mode);
                String sourceName = (String) publishEntity.get(EntityPublishConstant.field_source_name);

                // 检查：是否为空
                if (MethodUtils.hasEmpty(sourceType, publishMode, sourceName)) {
                    continue;
                }

                // 检查：是否为状态发布模式
                if (!mode.equals(publishMode)) {
                    continue;
                }

                result.put(entityType, publishEntity);
            }
        }

        return result;
    }
}
