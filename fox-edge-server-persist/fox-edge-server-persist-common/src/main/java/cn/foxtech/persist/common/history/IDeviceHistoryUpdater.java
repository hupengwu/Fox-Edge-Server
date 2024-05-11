package cn.foxtech.persist.common.history;

import cn.foxtech.common.entity.entity.DeviceValueEntity;

import java.util.Map;

public interface IDeviceHistoryUpdater {
    void saveHistoryEntity(DeviceValueEntity existValueEntity, Map<String, Object> statusValues);
    void clearHistoryEntity();
}
