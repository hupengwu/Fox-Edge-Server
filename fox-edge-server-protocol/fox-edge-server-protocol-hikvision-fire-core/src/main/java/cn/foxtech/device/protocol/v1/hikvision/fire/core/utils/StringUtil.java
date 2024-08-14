/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.hikvision.fire.core.utils;

public class StringUtil {
    public static String truncateString(String str, int length) {
        if (str.length() == length) {
            return str;
        }

        // IMEI(8 字节):BCD格式
        StringBuilder sb = new StringBuilder();
        if (str.length() < length) {
            for (int i = 0; i < length - str.length(); i++) {
                sb.append("0");
            }
            sb.append(str);
        }
        if (str.length() >= length) {
            for (int i = 0; i < str.length(); i++) {
                sb.append(str.charAt(i));
            }
        }

        return sb.toString();
    }
}
