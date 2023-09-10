package cn.foxtech.device.protocol.v1.utils;

public class ByteUtils {
    /**
     * 对16位长度byte[]，进行int解码
     *
     * @param data     数组
     * @param offset   偏移量
     * @param msbFirst 高位在前
     * @return 数值
     */
    public static int decodeInt16(byte[] data, int offset, boolean msbFirst) {
        if (msbFirst) {
            int value = 0;
            value += (data[offset + 0] & 0xff) * 0x100;
            value += (data[offset + 1] & 0xff) * 0x1;
            return value;
        } else {
            int value = 0;
            value += (data[offset + 1] & 0xff) * 0x100;
            value += (data[offset + 0] & 0xff) * 0x1;
            return value;
        }
    }

    /**
     * 对16位长度byte[]，进行int解码
     *
     * @param data     数组
     * @param msbFirst 高位在前
     * @return 数值
     */
    public static int decodeInt16(byte[] data, boolean msbFirst) {
        return decodeInt16(data, 0, msbFirst);
    }

    /**
     * 对16位长度byte[]，进行int解码
     *
     * @param data 数组
     * @return 数值
     */
    public static int decodeInt16(byte[] data) {
        return decodeInt16(data, true);
    }

    /**
     * 对32位长度byte[]，进行int解码
     *
     * @param data     数组
     * @param offset   偏移量
     * @param msbFirst 高位在前
     * @return 数值
     */
    public static long decodeInt32(byte[] data, int offset, boolean msbFirst) {
        if (msbFirst) {
            long value = 0;
            value += (data[offset + 0] & 0xff) * 0x1000000L;
            value += (data[offset + 1] & 0xff) * 0x10000L;
            value += (data[offset + 2] & 0xff) * 0x100L;
            value += (data[offset + 3] & 0xff) * 0x1L;
            return value;
        } else {
            long value = 0;
            value += (data[offset + 3] & 0xff) * 0x1000000L;
            value += (data[offset + 2] & 0xff) * 0x10000L;
            value += (data[offset + 1] & 0xff) * 0x100L;
            value += (data[offset + 0] & 0xff) * 0x1L;
            return value;
        }
    }

    /**
     * 对32位长度byte[]，进行int解码
     *
     * @param data     数组
     * @param msbFirst 高位在前
     * @return 数值
     */
    public static long decodeInt32(byte[] data, boolean msbFirst) {
        return decodeInt32(data, 0, msbFirst);
    }

    /**
     * 对32位长度byte[]，进行int解码
     *
     * @param data     数组
     * @return 数值
     */
    public static long decodeInt32(byte[] data) {
        return decodeInt32(data, 0, true);
    }
}
