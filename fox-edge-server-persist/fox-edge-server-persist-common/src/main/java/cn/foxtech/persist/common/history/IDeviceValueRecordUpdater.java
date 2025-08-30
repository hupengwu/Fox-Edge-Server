package cn.foxtech.persist.common.history;

import cn.foxtech.common.entity.entity.DeviceValueEntity;

public interface IDeviceValueRecordUpdater {
    void saveDeviceValueRecord(DeviceValueEntity valueEntity);

    void clearDeviceValueRecordEntity();
}
