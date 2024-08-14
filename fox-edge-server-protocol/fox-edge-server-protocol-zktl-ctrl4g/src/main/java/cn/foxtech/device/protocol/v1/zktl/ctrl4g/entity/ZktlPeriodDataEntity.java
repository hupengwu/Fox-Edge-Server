/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlPeriodDataEntity extends ZktlDataEntity {
    /**
     * ICCID
     */
    private String iccid = "";
    /**
     * 供电类型
     */
    private int powerSupplyType = 0;
    /**
     * 电池电压
     */
    private int voltage = 0;
    /**
     * ADC0
     */
    private int adc0 = 0;
    /**
     * ADC1
     */
    private int adc1 = 0;
    /**
     * 继电器状态
     */
    private int relayStatus = 0;
    /**
     * 外部输入 IO
     */
    private int outIO = 0;
    /**
     * 信号 CSQ
     */
    private int signalCSQ = 0;
    /**
     * 预留
     */
    private int reserve = 0;
}
