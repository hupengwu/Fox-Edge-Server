package cn.foxtech.persist.mysql.history;


import cn.foxtech.common.entity.entity.DeviceHistoryEntity;
import cn.foxtech.common.entity.entity.DeviceHistoryPo;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.service.devicehistory.DeviceHistoryEntityMaker;
import cn.foxtech.persist.common.history.IDeviceHistoryUpdater;
import cn.foxtech.persist.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceHistoryMySQLUpdater implements IDeviceHistoryUpdater {
    private static final Logger logger = Logger.getLogger(DeviceHistoryMySQLUpdater.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private InitialConfigService configService;

    /**
     * 上次处理时间
     */
    private long lastTime = 0;

    @Override
    public void saveHistoryEntity(DeviceValueEntity existValueEntity, Map<String, Object> statusValues) {
        if (existValueEntity == null) {
            return;
        }

        long time = System.currentTimeMillis();

        try {
            for (Map.Entry<String, Object> entry : statusValues.entrySet()) {
                String paramName = entry.getKey();
                Object paramValue = entry.getValue();

                if (!this.isValue(paramValue)) {
                    continue;
                }

                // 检查：数据是否发生了变化
                if (!this.isChanged(existValueEntity, paramName, paramValue)) {
                    continue;
                }

                // 构造历史记录对象
                DeviceHistoryEntity deviceHistoryEntity = new DeviceHistoryEntity();
                deviceHistoryEntity.setDeviceId(existValueEntity.getId());
                deviceHistoryEntity.setObjectName(paramName);
                deviceHistoryEntity.setParamValue(paramValue);
                deviceHistoryEntity.setCreateTime(time);

                // 保存历史记录
                DeviceHistoryPo deviceHistoryPo = DeviceHistoryEntityMaker.makeEntity2Po(deviceHistoryEntity);
                this.entityManageService.getDeviceHistoryEntityService().insertEntity(deviceHistoryPo);
            }
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    private boolean isValue(Object paramValue) {
        if (paramValue instanceof Double) {
            return true;
        }

        if (paramValue instanceof Float) {
            return true;
        }

        if (paramValue instanceof Integer) {
            return true;
        }

        if (paramValue instanceof Long) {
            return true;
        }

        return paramValue instanceof Short;

    }

    private boolean isChanged(DeviceValueEntity existValueEntity, String paramName, Object paramValue) {
        if (existValueEntity == null) {
            return true;
        }

        DeviceObjectValue existValue = existValueEntity.getParams().get(paramName);
        if (existValue == null) {
            return true;
        }

        if (paramValue == null) {
            return false;
        }

        return !paramValue.equals(existValue.getValue());
    }

    @Override
    public void clearHistoryEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("deviceHistory", new HashMap<>());

            Integer maxCount = (Integer) params.getOrDefault("maxCount", 1000000);
            Integer period = (Integer) params.getOrDefault("period", 3600);

            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            // 除了最近的maxCount条数据，旧数据全部删除
            this.entityManageService.getDeviceHistoryEntityService().delete(maxCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
