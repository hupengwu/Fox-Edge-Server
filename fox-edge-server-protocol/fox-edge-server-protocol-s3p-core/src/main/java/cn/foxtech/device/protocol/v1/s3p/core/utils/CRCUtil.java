package cn.foxtech.device.protocol.v1.s3p.core.utils;

/**
 * CRCUtil
 */
public class CRCUtil {
    private static final int POLYNOMIAL = 0x1021;

    private static final int INITIAL = 0x0000; // 初始值

    /**
     * CRC-16-XMODEM校验
     *
     * @param data   数据
     * @param length 长度
     * @return crc校验码
     */
    public static int calculateCRC16XMODEM(byte[] data, int length) {
        int crc = INITIAL;
        for (int i = 0; i < length; i++) {
            byte b = data[i];
            crc ^= (b & 0xFF) << 8; // XOR with byte value
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc & 0xFFFF;
    }
}
