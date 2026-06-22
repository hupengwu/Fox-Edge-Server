package cn.foxtech.persist.common.history;

import cn.foxtech.common.entity.entity.DeviceValueEntity;

import java.util.List;
import java.util.Map;

public interface IDeviceValueRecordUpdater {
    void saveDeviceValueRecord(List<DeviceValueEntity> valueEntityList, Map<String, Object> property);

    void clearDeviceValueRecordEntity();
}
