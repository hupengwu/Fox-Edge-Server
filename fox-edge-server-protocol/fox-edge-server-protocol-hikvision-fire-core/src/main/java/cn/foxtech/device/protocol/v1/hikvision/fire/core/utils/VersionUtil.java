/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.hikvision.fire.core.utils;

public class VersionUtil {
    public static String decodeVersion2byte(byte[] data, int offset) {
        return (data[offset + 0] & 0xff) + "." + String.format("%02d",data[offset + 1] & 0xff);
    }

    public static void encodeVersion2byte(String version, byte[] data, int offset) {
        if (version.isEmpty()) {
            return;
        }
        String[] item = version.split("\\.");
        if (item.length != 2){
            return;
        }

        data[offset + 0] = (byte) Integer.parseInt(item[0]);
        data[offset + 1] = (byte) Integer.parseInt(item[1]);
    }
}
