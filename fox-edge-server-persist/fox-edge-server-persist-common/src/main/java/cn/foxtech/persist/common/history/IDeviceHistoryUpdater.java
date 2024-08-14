/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.history;

import cn.foxtech.common.entity.entity.DeviceValueEntity;

import java.util.Map;

public interface IDeviceHistoryUpdater {
    void saveHistoryEntity(DeviceValueEntity existValueEntity, Map<String, Object> statusValues);
    void clearHistoryEntity();
}
