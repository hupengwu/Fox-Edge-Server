/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.entity;

import lombok.Data;

/**
 * S帧结构的控制实体：面向控制阶段的确认
 */
@Data
public class SControlEntity extends ControlEntity{
    /**
     * 接收序列号
     */
    private short accept;
}
