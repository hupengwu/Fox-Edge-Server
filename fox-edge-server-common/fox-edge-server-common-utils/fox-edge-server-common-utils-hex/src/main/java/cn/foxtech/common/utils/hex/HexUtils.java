/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package cn.foxtech.common.utils.hex;

/**
 * 十六进制数组的转换
 *
 * @author h00442047
 * @since 2019/9/20
 */
public class HexUtils {
    /**
     * 根据十六进制生成byte
     *
     * @param hex 16进制的字符串 比如"FF"
     * @return byte数字比如-1
     */
    public static byte hex2byte(String hex) {
        return Integer.valueOf(hex, 16).byteValue();
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] hexStringToByteArray(String hexString) {
        String string = hexString.replaceAll(" ", "");
        final byte[] byteArray = new byte[string.length() / 2];
        hexStringToByteArray(string, byteArray, 0);
        return byteArray;
    }

    public static void hexStringToByteArray(String hexString, byte[] byteArray, int offset) {
        int length = hexString.length() / 2;
        if (byteArray.length < length) {
            length = byteArray.length;
        }

        int pos = 0;
        for (int i = 0; i < length; i++) {
            // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(pos), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(pos + 1), 16) & 0xff);
            byteArray[offset + i] = (byte) (high << 4 | low);
            pos += 2;
        }
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String byteArrayToHexString(byte[] byteArray) {
        return byteArrayToHexString(byteArray, false);
    }

    public static String byteArrayToHexString(byte[] byteArray, boolean blankz) {
        return byteArrayToHexString(byteArray, 0, byteArray.length, blankz);
    }

    public static String byteArrayToHexString(byte[] byteArray, int offset, int length, boolean blankz) {
        final StringBuilder hexString = new StringBuilder();
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            if ((byteArray[i] & 0xff) < 0x10) {
                // 0~F前面不零
                hexString.append("0");
            }

            hexString.append(Integer.toHexString(0xFF & byteArray[i]));

            if (blankz) {
                hexString.append(" ");
            }
        }
        return hexString.toString();
    }
}
