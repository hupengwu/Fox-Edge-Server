/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.utils;

public class PackUtil {
    public static String byteArrayToHexString(int[] byteArray) {
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                // 0~F前面不零
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString();
    }

    public static String byteArrayToHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                // 0~F前面不零
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString();
    }

    public static String byteArray2String(int[] pack) {
        StringBuilder sb = new StringBuilder();
        for (int at : pack) {
            sb.append((char) at);
        }
        return sb.toString();
    }

    public static String byteArray2String(byte[] pack) {
        StringBuilder sb = new StringBuilder();
        for (int at : pack) {
            sb.append((char) at);
        }
        return sb.toString();
    }
}
