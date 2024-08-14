/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlNbDataEntity extends ZktlDataEntity {
    /**
     * IMEI
     */
    private String imei = "";
    /**
     * ICCID
     */
    private String iccid = "";
    /**
     *  防拆状态
     */
    private int tamper = 0;
    /**
     * 外部触发状态
     */
    private int externalTrigger = 0;
    /**
     * 外部开关 2 状态
     */
    private int externalSwitch2 = 0;
    /**
     * 外部开关 1 状态
     */
    private int externalSwitch1 = 0;
    /**
     * 包类型
     */
    private int packType = 0;
    /**
     * 电池电压
     */
    private double batteryVoltage = 0;
    /**
     * 采集数据
     */
    private double collectData = 0;
    /**
     * 信号强度
     */
    private int signal = 0;
    /**
     * 包序号
     */
    private int packSn = 0;
    /**
     * 预留
     */
    private int reserve = 0;

    public String getServiceKey() {
        return "air5in1=" + super.getCommunTypeName() + ":" + super.getDeviceTypeName() + ":" + this.imei + ":" + this.iccid;
    }
}
