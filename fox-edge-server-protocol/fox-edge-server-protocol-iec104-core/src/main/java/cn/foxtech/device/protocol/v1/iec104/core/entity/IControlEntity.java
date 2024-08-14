/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.entity;

import lombok.Data;

/**
 * I帧结构的控制实体：面向控制和信息获取
 */
@Data
public class IControlEntity extends ControlEntity {
    /**
     * 发送序列号N(S)
     */
    private short send;

    /**
     * 接收序列号N(R)
     */
    private short accept;
}
