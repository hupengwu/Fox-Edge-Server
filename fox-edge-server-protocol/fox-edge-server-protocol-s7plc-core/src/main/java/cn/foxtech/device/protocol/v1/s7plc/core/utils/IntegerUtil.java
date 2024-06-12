/*
 * MIT License
 *
 * Copyright (c) 2021-2099 Oscura (xingshuang) <xingshuang_cool@163.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.foxtech.device.protocol.v1.s7plc.core.utils;


/**
 * @author xingshuang
 */
public class IntegerUtil {

    private IntegerUtil() {
        // NOOP
    }

    /**
     * 将short转换为字节数组
     *
     * @param data         short数据
     * @param littleEndian true:小端，false：大端
     * @return 字节数组
     */
    public static byte[] toByteArray(int data, boolean littleEndian) {
        byte[] bytes = new byte[4];

        if (littleEndian) {
            bytes[0] = (byte) ((data) & 0xFF);
            bytes[1] = (byte) ((data >> 8) & 0xFF);
            bytes[2] = (byte) ((data >> 16) & 0xFF);
            bytes[3] = (byte) ((data >> 24) & 0xFF);
        } else {
            bytes[0] = (byte) ((data >> 24) & 0xFF);
            bytes[1] = (byte) ((data >> 16) & 0xFF);
            bytes[2] = (byte) ((data >> 8) & 0xFF);
            bytes[3] = (byte) ((data) & 0xFF);
        }
        return bytes;
    }

    /**
     * 将int转换为字节数组，默认采用大端模式
     *
     * @param data int数据
     * @return 字节数组
     */
    public static byte[] toByteArray(int data) {
        return toByteArray(data, false);
    }

    /**
     * 将long转换为字节数组，默认采用大端模式
     *
     * @param data long数据
     * @return 字节数组
     */
    public static byte[] toByteArray(long data) {
        return toByteArray((int) data, false);
    }

    /**
     * 将long转换为字节数组，并自定义获取字节数，默认采用大端模式
     *
     * @param data   long数据
     * @param offset 偏移量
     * @param length 读取长度
     * @return 字节数组
     */
    public static byte[] toCustomByteArray(long data, int offset, int length) {
        return toCustomByteArray(data, offset, length, false);
    }

    /**
     * 将long转换为字节数组，并自定义获取字节数，默认采用大端模式
     *
     * @param data         long数据
     * @param offset       偏移量
     * @param length       读取长度
     * @param littleEndian 小端模式
     * @return 字节数组
     */
    public static byte[] toCustomByteArray(long data, int offset, int length, boolean littleEndian) {
        if (offset + length > 4) {
            throw new IndexOutOfBoundsException("offset + length > 4");
        }
        byte[] bytes = toByteArray((int) data, littleEndian);
        byte[] res = new byte[length];
        System.arraycopy(bytes, offset, res, 0, length);
        return res;
    }

    /**
     * 将字节数组转换为int32
     *
     * @param data 字节数组
     * @return int32数据
     */
    public static int toInt32(byte[] data) {
        return toInt32(data, 0, false);
    }

    /**
     * 将字节数组转换为int32
     *
     * @param data   字节数组
     * @param offset 偏移量
     * @return int32数据
     */
    public static int toInt32(byte[] data, int offset) {
        return toInt32(data, offset, false);
    }

    /**
     * 将字节数组转换为int32
     *
     * @param data         字节数组
     * @param offset       偏移量
     * @param littleEndian true：小端模式，false：大端模式
     * @return int32数据
     */
    public static int toInt32(byte[] data, int offset, boolean littleEndian) {
        if (data.length < 4) {
            throw new IndexOutOfBoundsException("data length < 4");
        }
        if (offset + 4 > data.length) {
            throw new IndexOutOfBoundsException("offset + 4 > data length");
        }
        int b = littleEndian ? 3 : 0;
        int d = littleEndian ? 1 : -1;
        return (((data[offset + b - d * 0] & 0xFF) << 24)
                | ((data[offset + b - d * 1] & 0xFF) << 16)
                | ((data[offset + b - d * 2] & 0xFF) << 8)
                | ((data[offset + b - d * 3] & 0xFF) << 0));
    }

    /**
     * 将字节数组转换为int32
     *
     * @param data   字节数组
     * @param offset 偏移量
     * @return int32数据
     */
    public static int toInt32In3Bytes(byte[] data, int offset) {
        return toInt32In3Bytes(data, offset, false);
    }

    /**
     * 将字节数组转换为int32
     *
     * @param data         字节数组
     * @param offset       偏移量
     * @param littleEndian true：小端模式，false：大端模式
     * @return int32数据
     */
    public static int toInt32In3Bytes(byte[] data, int offset, boolean littleEndian) {
        if (data.length < 3) {
            throw new IndexOutOfBoundsException("data length < 3");
        }
        if (offset + 3 > data.length) {
            throw new IndexOutOfBoundsException("offset + 3 > data length");
        }
        int b = littleEndian ? 2 : 0;
        int d = littleEndian ? 1 : -1;
        return (((data[offset + b - d * 0] & 0xFF) << 16)
                | ((data[offset + b - d * 1] & 0xFF) << 8)
                | ((data[offset + b - d * 2] & 0xFF) << 0));
    }

    /**
     * 将字节数组转换为uint32
     *
     * @param data 字节数组
     * @return uint32数据
     */
    public static long toUInt32(byte[] data) {
        return toUInt32(data, 0, false);
    }

    /**
     * 将字节数组转换为uint32
     *
     * @param data   字节数组
     * @param offset 偏移量
     * @return uint32数据
     */
    public static long toUInt32(byte[] data, int offset) {
        return toUInt32(data, offset, false);
    }

    /**
     * 将字节数组转换为uint32
     *
     * @param data         字节数组
     * @param offset       偏移量
     * @param littleEndian true：小端模式，false：大端模式
     * @return uint32数据
     */
    public static long toUInt32(byte[] data, int offset, boolean littleEndian) {
        if (data.length < 4) {
            throw new IndexOutOfBoundsException("data length < 4");
        }
        if (offset + 4 > data.length) {
            throw new IndexOutOfBoundsException("offset + 4 > data length");
        }
        int b = littleEndian ? 3 : 0;
        int d = littleEndian ? 1 : -1;
        return (((data[offset + b - d * 0] & 0xFF) << 24)
                | ((data[offset + b - d * 1] & 0xFF) << 16)
                | ((data[offset + b - d * 2] & 0xFF) << 8)
                | ((data[offset + b - d * 3] & 0xFF) << 0)) & 0xFFFFFFFFL;
    }
}
