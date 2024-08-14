/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.core.context;

import java.util.Map;

public interface IApplicationContext {
    /**
     * 获得设备模型
     *
     * @param modelName 模型名称
     * @return 模型的结构
     */
    Map<String, Object> getDeviceModels(String modelName);
}
