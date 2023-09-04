package cn.foxtech.device.protocol.v1.modbus.core;

/**
 * ModBus常量定义
 */
public class ModBusConstants {
    public static final String MODE = "mode";
    public static final String MODE_ASCII = "ASCII";
    public static final String MODE_RTU = "RTU";
    public static final String MODE_TCP = "TCP";

    /**
     * ModBus的报文结构
     */
    public static final String SN = "SN";
    public static final String ADDR = "ADDR";
    public static final String FUNC = "FUNC";
    public static final String DATA = "DATA";

    /**
     * 寄存器地址和数量
     */
    public static final String REG_ADDR = "REG_ADDR";
    public static final String REG_CNT = "REG_CNT";

    public static final String REG_HOLD_STATUS = "REG_HOLD_STATUS";
}
