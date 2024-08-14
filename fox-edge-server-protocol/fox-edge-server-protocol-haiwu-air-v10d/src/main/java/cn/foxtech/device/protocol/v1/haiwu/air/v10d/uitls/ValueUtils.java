/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haiwu.air.v10d.uitls;

public class ValueUtils {
    public static int decodeInteger(byte[] data, int offset) {
        return (data[offset + 0] & 0xff) * 0x100 + (data[offset + 1] & 0xff);
    }
}
