/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.huawei.iotda.service.entity.utils;

public class ServiceIdUtils {
    public static String getServiceId(String deviceType) {
        deviceType = deviceType.replace("+", "_");
        deviceType = deviceType.replace(" ", "_");
        return deviceType;
    }
}
