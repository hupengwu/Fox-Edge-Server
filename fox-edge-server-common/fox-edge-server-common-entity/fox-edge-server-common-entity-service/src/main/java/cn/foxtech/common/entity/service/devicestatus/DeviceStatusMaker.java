/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.devicestatus;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;

import java.util.ArrayList;
import java.util.List;

public class DeviceStatusMaker {
    public static List<BaseEntity> buildDeviceValueList(List<BaseEntity> deviceList) {
        List<BaseEntity> deviceStatusList = new ArrayList<>();
        for (BaseEntity entity : deviceList) {
            DeviceEntity device = (DeviceEntity) entity;
            DeviceStatusEntity value = new DeviceStatusEntity();
            value.setId(device.getId());

            deviceStatusList.add(value);
        }

        return deviceStatusList;
    }
}
