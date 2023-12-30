package cn.foxtech.device.protocol.v1.utils;

public class BcdUtils {
    public static void str2bcd(String bcd, byte[] bcdByte, int offset, boolean bigEndian) {
        // 获取字节数组长度
        int size = bcd.length() / 2;
        int remainder = bcd.length() % 2;


        if (bigEndian) {
            for (int i = 0; i < size; i++) {
                int high = Integer.parseInt(bcd.substring(2 * i, 2 * i + 1));
                int low = Integer.parseInt(bcd.substring(2 * i + 1, 2 * i + 2));
                bcdByte[offset + i] = (byte) ((high << 4) | low);
            }

            // 如果存在余数，需要填F
            if (remainder > 0) {
                int low = Integer.parseInt(bcd.substring(bcd.length() - 1));
                bcdByte[offset + size - 1] = (byte) ((0xf << 4) | low);
            }
        } else {
            for (int i = size - 1; 0 <= i; i--) {
                int high = Integer.parseInt(bcd.substring(2 * i, 2 * i + 1));
                int low = Integer.parseInt(bcd.substring(2 * i + 1, 2 * i + 2));
                bcdByte[offset + i] = (byte) ((high << 4) | low);
            }

            // 如果存在余数，需要填F
            if (remainder > 0) {
                int low = Integer.parseInt(bcd.substring(bcd.length() - 1));
                bcdByte[offset + size - 1] = (byte) ((0xf << 4) | low);
            }
        }
    }

    /**
     * 数字字符串编成BCD格式字节数组
     *
     * @param bcd       数据
     * @param bigEndian 大端模式
     * @return 数据块
     */
    public static byte[] str2bcd(String bcd, boolean bigEndian) {
        // 获取字节数组长度
        int size = bcd.length() / 2;
        int remainder = bcd.length() % 2;

        // 分配空间
        byte[] bcdByte = new byte[size + remainder];

        str2bcd(bcd, bcdByte, 0, bigEndian);

        return bcdByte;
    }

    /**
     * BCD格式的字节数组解成数字字符串
     *
     * @param bcd 字节数组
     * @return 解码得到的字符串
     */
    public static String bcd2str(byte[] bcd) {
        if (null == bcd || bcd.length == 0) {
            return "";
        }

        return bcd2str(bcd, 0, bcd.length, false);
    }

    /**
     * 转BCD编码
     *
     * @param data      数据块
     * @param offset    偏移量
     * @param length    长度
     * @param bigEndian 是否为大端模式
     * @return 可读的字符串
     */
    public static String bcd2str(byte[] data, int offset, int length, boolean bigEndian) {
        // 存储转码后的字符串
        StringBuilder sb = new StringBuilder();


        if (bigEndian) {
            int end = offset + length;
            for (int i = offset; i < end; i++) {
                int low = (data[i] & 0x0f);
                int high = ((data[i] & 0xf0) >> 4);

                if (low > 9) {
                    low = 9;
                }
                if (high > 9) {
                    high = 9;
                }

                sb.append(high);
                sb.append(low);
            }
        } else {
            int end = offset + length - 1;
            for (int i = end; offset <= i; i--) {
                int low = (data[i] & 0x0f);
                int high = ((data[i] & 0xf0) >> 4);

                if (low > 9) {
                    low = 9;
                }
                if (high > 9) {
                    high = 9;
                }

                sb.append(high);
                sb.append(low);
            }
        }


        // 返回解码字符串
        return sb.toString();
    }

    public static int bcd2int(byte bcd) {
        return ((bcd & 0xf0) >> 4) * 10 + (bcd & 0x0f);
    }
}
