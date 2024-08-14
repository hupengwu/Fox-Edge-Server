/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.utils.enums;

import lombok.Getter;


public enum CrcType {
    CRC16IBM("CRC-16-IBM", 16, 0x8005, 0x0000, 0x0000, true),//
    CRC16MAXIM("CRC-16-MAXIM", 16, 0x8005, 0x0000, 0xFFFF, true),//
    CRC16USB("CRC-16-USB", 16, 0x8005, 0xFFFF, 0xFFFF, true),//
    CRC16MODBUS("CRC-16-MODBUS", 16, 0x8005, 0xFFFF, 0x0000, true),//
    CRC16CCITT("CRC-16-CCITT", 16, 0x1021, 0x0000, 0x0000, true),//
    CRC16CCITT_FALSE("CRC-16-CCITT-FALSE", 16, 0x1021, 0xFFFF, 0x0000, false),//
    CRC16X25("CRC-16-X25", 16, 0x1021, 0xFFFF, 0xFFFF, true),//
    CRC16XMODEM("CRC-16-XMODEM", 16, 0x1021, 0x0000, 0x0000, false),//
    CRC16XMODEM2("CRC-16-XMODEM2", 16, 0x8408, 0x0000, 0x0000, true),//
    CRC16DNP("CRC-16-DNP", 16, 0x3D65, 0x0000, 0xFFFF, true),//


    ;
    @Getter
    private final String name;

    @Getter
    private final int length;

    @Getter
    private final int polynomial;

    @Getter
    private final int initial;

    @Getter
    private final int xorOut;
    @Getter
    private final boolean refInOut;


    CrcType(String name, int length, int polynomial, int initial, int xorOut, boolean refInOut) {
        this.name = name;
        this.length = length;
        this.polynomial = polynomial;
        this.initial = initial;
        this.xorOut = xorOut;
        this.refInOut = refInOut;
    }

    public static CrcType getEnum(String name) {
        for (CrcType value : CrcType.values()) {
            if (name.equals(value.name)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
