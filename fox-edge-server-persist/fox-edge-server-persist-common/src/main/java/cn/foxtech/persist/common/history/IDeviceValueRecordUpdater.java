package cn.foxtech.persist.common.history;

import cn.foxtech.common.entity.entity.DeviceValueEntity;

import java.util.List;

public interface IDeviceValueRecordUpdater {
    void saveDeviceValueRecord(List<DeviceValueEntity> valueEntityList);

    void clearDeviceValueRecordEntity();
}
