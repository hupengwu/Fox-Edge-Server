/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlPduEntity {
    /**
     * 通信类型
     */
    private int communType = 0;
    /**
     * 设备类型
     */
    private int deviceType = 0;
    /**
     * 设备类型
     */
    private String data = "";

}
