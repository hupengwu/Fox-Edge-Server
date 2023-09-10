package cn.foxtech.device.protocol.v1.utils;

/**
 * 浮点数内存编码转换
 */
public class BitsUtils {
    /**
     * 4字节整数内存编码，转换为int
     *
     * @param bts 内存或者报文中的二进制数据编码信息
     * @return 整数信息
     */
    public static int bitsToInteger(byte[] bts) {
        int bits = (bts[0] & 0xff) + ((bts[1] & 0xff) << 8) + ((bts[2] & 0xff) << 16) + ((bts[3] & 0xff) << 24);
        return bits;
    }

    /**
     * 4字节整数内存编码，转换为int
     *
     * @param bts0  bts0
     * @param bts1 bts1
     * @param bts2 bts2
     * @param bts3 bts3
     * @return float的浮点数
     */
    public static float bitsToInteger(byte bts0, byte bts1, byte bts2, byte bts3) {
        int bits = (bts0 & 0xff) + ((bts1 & 0xff) << 8) + ((bts2 & 0xff) << 16) + ((bts3 & 0xff) << 24);
        return bits;
    }

    /**
     * 4字节浮点数内存编码，转换为float
     *
     * @param bts 内存或者报文中的二进制编码
     * @return 浮点数
     */
    public static float bitsToFloat(byte[] bts) {
        if (bts.length == 4) {
            int bits = 0;
            bits += (bts[0] & 0xff) << 0;
            bits += (bts[1] & 0xff) << 8;
            bits += (bts[2] & 0xff) << 16;
            bits += (bts[3] & 0xff) << 24;
            return Float.intBitsToFloat(bits);
        } else {
            return 0;
        }
    }

    /**
     * 4字节浮点数内存编码，转换为float
     *
     * @param bts0 bts0
     * @param bts1 bts1
     * @param bts2 bts2
     * @param bts3 bts3
     * @return 编码形成的浮点数
     */
    public static float bitsToFloat(byte bts0, byte bts1, byte bts2, byte bts3) {
        int bits = 0;
        bits += (bts0 & 0xff) << 0;
        bits += (bts1 & 0xff) << 8;
        bits += (bts2 & 0xff) << 16;
        bits += (bts3 & 0xff) << 24;

        return Float.intBitsToFloat(bits);
    }

    public static float bitsToFloat(int bits) {
        return Float.intBitsToFloat(bits);
    }

    /**
     * 8字节浮点数内存编码，转换为double
     *
     * @param bts bts
     * @return 编码形成的浮点数
     */
    public static double bitsToDouble(byte[] bts) {
        if (bts.length == 8) {
            long bits = (bts[0] & 0xff) + ((bts[1] & 0xff) << 8) + ((bts[2] & 0xff) << 16) + ((bts[3] & 0xff) << 24) + ((bts[4] & 0xff) << 32) + ((bts[5] & 0xff) << 40) + ((bts[6] & 0xff) << 48) + ((bts[7] & 0xff) << 56);
            return Double.longBitsToDouble(bits);
        } else {
            return 0;
        }
    }

    /**
     * 8字节浮点数内存编码，转换为double
     * @param bts0 bts0
     * @param bts1 bts1
     * @param bts2 bts2
     * @param bts3 bts3
     * @param bts4 bts4
     * @param bts5 bts5
     * @param bts6 bts6
     * @param bts7 bts7
     * @return 解码出来的浮点数
     */
    public static double bitsToDouble(byte bts0, byte bts1, byte bts2, byte bts3, byte bts4, byte bts5, byte bts6, byte bts7) {
        long bits = (bts0 & 0xff) + ((bts1 & 0xff) << 8) + ((bts2 & 0xff) << 16) + ((bts3 & 0xff) << 24) + ((bts4 & 0xff) << 32) + ((bts5 & 0xff) << 40) + ((bts6 & 0xff) << 48) + ((bts7 & 0xff) << 56);
        return Double.longBitsToDouble(bits);
    }
}
