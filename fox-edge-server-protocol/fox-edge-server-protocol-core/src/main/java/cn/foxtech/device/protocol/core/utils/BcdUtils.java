package cn.foxtech.device.protocol.core.utils;

public class BcdUtils {

    /**
     * 数字字符串编成BCD格式字节数组
     *
     * @param bcd 数字字符串
     * @return 数据编码
     */
    public static byte[] str2bcd(String bcd) {
        // 获取字节数组长度
        int size = bcd.length() / 2;
        int remainder = bcd.length() % 2;

        // 存储BCD码字节
        byte[] bcdByte = new byte[size + remainder];

        // 转BCD码
        for (int i = 0; i < size; i++) {
            int low = Integer.parseInt(bcd.substring(2 * i, 2 * i + 1));
            int high = Integer.parseInt(bcd.substring(2 * i + 1, 2 * i + 2));
            bcdByte[i] = (byte) ((high << 4) | low);
        }

        // 如果存在余数，需要填F
        if (remainder > 0) {
            int low = Integer.parseInt(bcd.substring(bcd.length() - 1));
            bcdByte[bcdByte.length - 1] = (byte) ((0xf << 4) | low);
        }

        // 返回BCD码字节数组
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

        // 存储转码后的字符串
        StringBuilder sb = new StringBuilder();

        // 循环数组解码
        for (int i = 0; i < bcd.length; i++) {
            // 转换低字节
            int low = (bcd[i] & 0x0f);
            sb.append(low);

            // 转换高字节
            int high = ((bcd[i] & 0xf0) >> 4);

            // 如果高字节等于0xf说明是补的字节，直接抛掉
            if (high != 0xf) {
                sb.append(high);
            }
        }

        // 返回解码字符串
        return sb.toString();
    }

    public static int bcd2int(byte bcd) {
        return ((bcd & 0xf0) >> 4) * 10 + (bcd & 0x0f);
    }
}
