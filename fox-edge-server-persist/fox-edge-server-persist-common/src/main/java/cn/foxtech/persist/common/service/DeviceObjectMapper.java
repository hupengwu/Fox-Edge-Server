package cn.foxtech.persist.common.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceObjectMapper {
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 映射表
     */
    private Map<String, Object> mapperEntityMap;

    public Pair<String, Integer> getValue(String deviceType, String objectName) {
        return (Pair<String, Integer>) Maps.getValue(this.mapperEntityMap, deviceType, objectName);
    }

    public Map<String, Object> getValue(String deviceType) {
        return (Map<String, Object>) Maps.getValue(this.mapperEntityMap, deviceType);
    }

    public void syncEntity() {
        // 检查：是否有重新状态的配置到达
        Long updateTime = this.entityManageService.removeReloadedFlag(DeviceMapperEntity.class.getSimpleName());
        if (updateTime == null && this.mapperEntityMap != null) {
            return;
        }

        // 取出重新状态的配置
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class);
        Map<String, Object> map = new HashMap<>();
        for (BaseEntity entity : entityList) {
            DeviceMapperEntity mapperEntity = (DeviceMapperEntity) entity;

            // 检查数据
            if (MethodUtils.hasEmpty(mapperEntity.getDeviceType(), mapperEntity.getObjectName(), mapperEntity.getMapperName(), mapperEntity.getMapperMode())) {
                continue;
            }

            // 构造数据
            Pair<String, Integer> pair = new Pair(mapperEntity.getMapperName(), mapperEntity.getMapperMode());
            Maps.setValue(map, mapperEntity.getDeviceType(), mapperEntity.getObjectName(), pair);
        }

        this.mapperEntityMap = map;
    }
}
