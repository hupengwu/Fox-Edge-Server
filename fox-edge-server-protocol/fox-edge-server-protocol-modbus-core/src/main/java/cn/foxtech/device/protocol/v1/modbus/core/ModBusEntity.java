package cn.foxtech.device.protocol.v1.modbus.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * ModBus的三要素：地址，功能码，数据
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ModBusEntity {
    /**
     * 流水号
     */
    private int sn = 0;

    /**
     * 地址
     */
    private byte devAddr = 0x01;

    /**
     * 功能码
     */
    private byte func = 0x01;

    /**
     * 数据域
     */
    private byte[] data = new byte[0];

    /**
     * 出错信息
     */
    private int errCode = 0;

    /**
     * 出错信息
     */
    private String errMsg = "";
}
