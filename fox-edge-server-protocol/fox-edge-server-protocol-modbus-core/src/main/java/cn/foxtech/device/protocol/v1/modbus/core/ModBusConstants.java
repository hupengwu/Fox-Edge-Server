/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.modbus.core;

/**
 * ModBus常量定义
 */
public class ModBusConstants {
    public static final String MODE = "modbusMode";
    public static final String MODE_ASCII = "ASCII";
    public static final String MODE_RTU = "RTU";
    public static final String MODE_TCP = "TCP";

    /**
     * ModBus的报文结构
     */
    public static final String SN = "sn";
    public static final String ADDR = "devAddr";
    public static final String FUNC = "func";
    public static final String DATA = "data";

    /**
     * 寄存器地址和数量
     */
    public static final String REG_ADDR = "regAddr";
    public static final String REG_CNT = "regCnt";

    public static final String REG_HOLD_STATUS = "regHoldStatus";
}
