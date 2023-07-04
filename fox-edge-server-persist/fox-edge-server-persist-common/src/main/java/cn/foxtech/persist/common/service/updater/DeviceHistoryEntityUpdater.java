package cn.foxtech.persist.common.service.updater;

import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.common.entity.entity.DeviceHistoryEntity;
import cn.foxtech.common.entity.entity.DeviceHistoryPo;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.devicehistory.DeviceHistoryEntityMaker;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeviceHistoryEntityUpdater {
    private static final Logger logger = Logger.getLogger(DeviceHistoryEntityUpdater.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 保存历史记录
     */
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
}
