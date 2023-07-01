package cn.foxtech.device.protocol.iec104.core.encoder;

import java.io.ByteArrayOutputStream;
import java.util.*;


/**
 * 数值的编码
 */
public class ValueEncoder {
    /**
     * 连续性的信息体
     * 格式特点：【地址(3字节)】+【数据1】+【数据2】+【数据3】......
     *
     * @param address 地址
     * @param bodys   已经经过编码的数据
     * @return 报文编码
     */
    public static byte[] encodeInfoBody(int address, List<byte[]> bodys) {
        // 计算需要的数组长度
        int sumLen = 0;
        for (byte[] body : bodys) {
            sumLen += body.length;
        }

        // 预分配总数组
        byte[] data = new byte[3 + sumLen];

        // 填写起始地址
        data[0] = (byte) (address & 0xff);
        data[1] = (byte) ((address & 0xff00) >> 8);
        data[2] = 0;

        int offset = 3;
        for (byte[] body : bodys) {
            System.arraycopy(body, 0, data, offset, data.length);
            offset += body.length;
        }

        return data;
    }

    /**
     * 非连续性的信息体
     * 格式特点：【地址1(3字节)+数据1】+【地址2(3字节)+数据2】+【地址3(3字节)+数据3】......
     *
     * @param bodys 已经经过编码的数据
     * @return 报文编码
     */
    public static byte[] encodeInfoBody(Map<Integer, byte[]> bodys) {
        // 计算需要的数组长度
        int sumLen = 0;
        for (Map.Entry<Integer, byte[]> entry : bodys.entrySet()) {
            sumLen += 3;
            sumLen += entry.getValue().length;
        }

        // 预分配总数组
        byte[] data = new byte[3 + sumLen];

        int offset = 0;
        for (Map.Entry<Integer, byte[]> entry : bodys.entrySet()) {
            int address = entry.getKey();
            byte[] body = entry.getValue();

            // 填写地址
            data[offset + 0] = (byte) (address & 0xff);
            data[offset + 1] = (byte) ((address & 0xff00) >> 8);
            data[offset + 2] = 0;
            offset += 3;

            // 填写信息体
            System.arraycopy(body, 0, data, offset, body.length);
            offset += body.length;
        }

        return data;
    }

    /**
     * 分拆连续性的信息体
     *
     * @param data 报文数据
     * @param infoSize 每个信息体的尺寸大小
     * @return 报文编码
     */
    public static Map<Integer, byte[]> decodeInfoBody(byte[] data, int infoSize) {
        Map<Integer, byte[]> bodys = new HashMap<>();

        // 验证长度
        if (((data.length - 3) % infoSize) != 0) {
            return null;
        }

        // 地址
        int address = data[0] & 0xff;
        address += (data[1] & 0xff) * 0x100;
        if (data[2] != 0) {
            return null;
        }

        int num = (data.length - 3) / infoSize;
        for (int i = 0; i < num; i++) {
            byte[] info = new byte[infoSize];
            System.arraycopy(data, i * infoSize + 3, info, 0, info.length);

            bodys.put(address + i, info);
        }


        return bodys;
    }

    /**
     * CP56Time2a的编码
     *
     * @param date 报文数据
     * @return 报文编码
     */
    public static byte[] encodeCP56Time2a(Date date) {
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 毫秒需要转换成两个字节其中 低位在前高位在后
        // 先转换成short
        int millisecond = calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND);

        // 默认的高位在前
        byte[] millisecondByte = intToByteArray(millisecond);
        bOutput.write(millisecondByte[3]);
        bOutput.write(millisecondByte[2]);

        // 分钟 只占6个比特位 需要把前两位置为零
        bOutput.write((byte) calendar.get(Calendar.MINUTE));
        // 小时需要把前三位置零
        bOutput.write((byte) calendar.get(Calendar.HOUR_OF_DAY));
        // 星期日的时候 week 是0
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == Calendar.SUNDAY) {
            week = 7;
        } else {
            week--;
        }
        // 前三个字节是 星期 因此需要将星期向左移5位  后五个字节是日期  需要将两个数字相加 相加之前需要先将前三位置零
        bOutput.write((byte) (week << 5) + (calendar.get(Calendar.DAY_OF_MONTH)));
        // 前四字节置零
        bOutput.write((byte) ((byte) calendar.get(Calendar.MONTH) + 1));
        bOutput.write((byte) (calendar.get(Calendar.YEAR) - 2000));
        return bOutput.toByteArray();
    }

    /**
     * CP56Time2a的解码
     *
     * @param dataByte 报文编码
     * @return 日期数据
     */
    public static Date decodeCP56Time2a(byte[] dataByte) {
        int year = (dataByte[6] & 0x7F) + 2000;
        int month = dataByte[5] & 0x0F;
        int day = dataByte[4] & 0x1F;
        int hour = dataByte[3] & 0x1F;
        int minute = dataByte[2] & 0x3F;
        int second = dataByte[1] > 0 ? dataByte[1] : (dataByte[1] & 0xff);
        int millisecond = dataByte[0] > 0 ? dataByte[0] : (dataByte[0] & 0xff);
        millisecond = (second << 8) + millisecond;
        second = millisecond / 1000;
        millisecond = millisecond % 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar.getTime();
    }

    /**
     * 将整数转换为报文
     * @param i 整数
     * @return 报文
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 将short转换为报文
     * @param val 参数
     * @return 报文
     */
    public static byte[] shortToByteArray(short val) {
        byte[] b = new byte[2];
        b[0] = (byte) ((val >> 8) & 0xff);
        b[1] = (byte) (val & 0xff);
        return b;
    }

    /**
     * 将报文转换为整数
     * @param bytes 报文
     * @return 整数
     */
    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (3 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * 将报文转换为short
     * @param bytes 报文
     * @return short数据
     */
    public static short byteArrayToShort(byte[] bytes) {
        short value = 0;
        for (int i = 0; i < 2; i++) {
            int shift = (1 - i) * 8;
            value += (bytes[i] & 0xFF) << shift;
        }
        return value;
    }

    /**
     * 将数值转换为十六进制文本格式
     * @param array 数组
     * @return 16进制的文本
     */
    public static String byteArrayToHexString(byte[] array) {
        return byteArray2HexString(array, Integer.MAX_VALUE, false);
    }

    /**
     * 将数组转换为16进制文本
     * @param arrBytes 二进制报文
     * @param count 部分数据的长度
     * @param blank 大小写
     * @return 16进制文本
     */
    public static String byteArray2HexString(byte[] arrBytes, int count, boolean blank) {
        String ret = "";
        if (arrBytes == null || arrBytes.length < 1) {
            return ret;
        }
        if (count > arrBytes.length) {
            count = arrBytes.length;
        }
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            ret = Integer.toHexString(arrBytes[i] & 0xFF).toUpperCase();
            if (ret.length() == 1) {
                builder.append("0").append(ret);
            } else {
                builder.append(ret);
            }
            if (blank) {
                builder.append(" ");
            }
        }

        return builder.toString();

    }

    /**
     * 返回指定位置的数组
     *
     * @param bytes 数据
     * @param start  开始位置
     * @param length 截取长度
     * @return 报文
     */
    public static byte[] getByte(byte[] bytes, int start, int length) {
        byte[] ruleByte = new byte[length];
        int index = 0;
        while (index < length) {
            ruleByte[index++] = bytes[start++];
        }
        return ruleByte;
    }


    /**
     * 十六进制字符串转换成byte数组
     *
     * @param hexStr 16进制文本
     * @return 二进制数据
     */
    public static byte[] hexStringToBytes(String hexStr) {
        hexStr = hexStr.replaceAll(" ", "");
        hexStr = hexStr.toUpperCase();
        int len = (hexStr.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hexStr.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}
