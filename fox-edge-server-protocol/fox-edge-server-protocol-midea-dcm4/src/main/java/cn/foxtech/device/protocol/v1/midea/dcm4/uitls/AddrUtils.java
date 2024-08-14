/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4.uitls;

public class AddrUtils {
    public static int decode(byte addr) {
        return addr & 0x0f;
    }

    public static byte encode(int addr) {
        return (byte) (addr & 0x0f | 0xb0);
    }
}
