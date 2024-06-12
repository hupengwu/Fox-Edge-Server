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

package cn.foxtech.device.protocol.v1.s7plc.core.common.buff;


import cn.foxtech.device.protocol.v1.s7plc.core.utils.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 字节读取缓存
 *
 * @author xingshuang
 */
public class ByteReadBuff extends ByteBuffBase {

    /**
     * 数据
     */
    private final byte[] data;
    /**
     * 是否为小端模式，默认不是，为大端模式
     */
    private final boolean littleEndian;
    /**
     * 偏移量
     */
    private int offset;

    /**
     * 构造方法
     *
     * @param data 字节数组
     */
    public ByteReadBuff(byte[] data) {
        this(data, 0, false, EByteBuffFormat.DC_BA);
    }

    public ByteReadBuff(byte[] data, int offset) {
        this(data, offset, false, EByteBuffFormat.DC_BA);
    }

    public ByteReadBuff(byte[] data, int offset, boolean littleEndian) {
        this(data, offset, littleEndian, EByteBuffFormat.DC_BA);
    }

    public ByteReadBuff(byte[] data, EByteBuffFormat format) {
        this(data, 0, false, format);
    }

    public ByteReadBuff(byte[] data, boolean littleEndian) {
        this(data, 0, littleEndian, EByteBuffFormat.DC_BA);
    }

    public ByteReadBuff(byte[] data, int offset, boolean littleEndian, EByteBuffFormat format) {
        super(format);
        this.littleEndian = littleEndian;
        this.data = data;
        this.offset = offset;
    }

    public static ByteReadBuff newInstance(byte[] data) {
        return new ByteReadBuff(data);
    }

    public static ByteReadBuff newInstance(byte[] data, int offset) {
        return new ByteReadBuff(data, offset);
    }

    public static ByteReadBuff newInstance(byte[] data, int offset, boolean littleEndian) {
        return new ByteReadBuff(data, offset, littleEndian);
    }

    public static ByteReadBuff newInstance(byte[] data, boolean littleEndian) {
        return new ByteReadBuff(data, littleEndian);
    }

    public static ByteReadBuff newInstance(byte[] data, EByteBuffFormat format) {
        return new ByteReadBuff(data, format);
    }

    public static ByteReadBuff newInstance(byte[] data, int offset, boolean littleEndian, EByteBuffFormat format) {
        return new ByteReadBuff(data, offset, littleEndian, format);
    }

    /**
     * 获取剩余字节数量
     *
     * @return 剩余字节数量
     */
    public int getRemainSize() {
        return this.data.length - this.offset;
    }

    /**
     * 校验条件
     *
     * @param index 索引
     */
    private void checkCondition(int index) {
        if (index < 0) {
            // 索引不能小于0
            throw new IndexOutOfBoundsException("Index less than 0");
        }
        if (index >= data.length) {
            // 超过字节数组最大容量
            throw new IndexOutOfBoundsException("Exceeds the maximum capacity of the byte array");
        }
    }

    /**
     * 获取boolean类型数据
     *
     * @param bit 位地址
     * @return boolean数据
     */
    public boolean getBoolean(int bit) {
        boolean res = this.getBoolean(this.offset, bit);
        this.offset++;
        return res;
    }

    /**
     * 获取1个字节数据
     *
     * @return 字节数据
     */
    public byte getByte() {
        byte res = this.getByte(this.offset);
        this.offset++;
        return res;
    }

    /**
     * 获取剩余所有字节
     *
     * @return 字节数组
     */
    public byte[] getBytes() {
        int length = this.data.length - this.offset;
        return this.getBytes(length);
    }

    /**
     * 获取字节数组数据
     *
     * @param length 长度
     * @return 字节数组
     */
    public byte[] getBytes(int length) {
        byte[] res = this.getBytes(this.offset, length);
        this.offset += length;
        return res;
    }

    /**
     * 获取1个字节的整形数据
     *
     * @return int数据
     */
    public int getByteToInt() {
        int res = this.getByteToInt(this.offset);
        this.offset++;
        return res;
    }

    /**
     * 获取int16数据
     *
     * @return int16数据
     */
    public short getInt16() {
        short res = this.getInt16(this.offset);
        this.offset += 2;
        return res;
    }

    /**
     * 获取uint16数据
     *
     * @return uint16数据
     */
    public int getUInt16() {
        int res = this.getUInt16(this.offset);
        this.offset += 2;
        return res;
    }

    /**
     * 获取int32数据
     *
     * @return int32数据
     */
    public int getInt32() {
        int res = this.getInt32(this.offset);
        this.offset += 4;
        return res;
    }

    /**
     * 获取uint32数据
     *
     * @return uint32数据
     */
    public long getUInt32() {
        long res = this.getUInt32(this.offset);
        this.offset += 4;
        return res;
    }

    /**
     * 获取float32数据
     *
     * @return float32数据
     */
    public float getFloat32() {
        float res = this.getFloat32(this.offset);
        this.offset += 4;
        return res;
    }

    /**
     * 获取float64数据
     *
     * @return float64数据
     */
    public double getFloat64() {
        double res = this.getFloat64(this.offset);
        this.offset += 8;
        return res;
    }

    /**
     * 获取字符串数据
     *
     * @param length 字符串长度
     * @return 字符串数据
     */
    public String getString(int length) {
        String res = this.getString(this.offset, length);
        this.offset += length;
        return res;
    }

    /**
     * 获取字符串数据
     *
     * @param length  字符串长度
     * @param charset 字符集
     * @return 字符串数据
     */
    public String getString(int length, Charset charset) {
        String res = this.getString(this.offset, length, charset);
        this.offset += length;
        return res;
    }

    /**
     * 获取boolean数据
     *
     * @param index 索引
     * @param bit   位
     * @return boolean数据
     */
    public boolean getBoolean(int index, int bit) {
        this.checkCondition(index);
        return BooleanUtil.getValue(this.data[index], bit);
    }

    /**
     * 获取一位的int值数据
     *
     * @param index 索引
     * @param bit   位
     * @return boolean数据
     */
    public int getBitToInt(int index, int bit) {
        this.checkCondition(index);
        return BooleanUtil.getValueToInt(this.data[index], bit);
    }

    /**
     * 获取字节数据
     *
     * @param index 索引
     * @return 字节
     */
    public byte getByte(int index) {
        this.checkCondition(index);
        return this.data[index];
    }

    /**
     * 获取字节数组数据
     *
     * @param index  索引
     * @param length 长度
     * @return 字节数组
     */
    public byte[] getBytes(int index, int length) {
        this.checkCondition(index + length - 1);
        return Arrays.copyOfRange(this.data, index, index + length);
    }

    /**
     * 获取一个字节的整形数据
     *
     * @param index 索引
     * @return int数据
     */
    public int getByteToInt(int index) {
        this.checkCondition(index);
        return ByteUtil.toUInt8(this.data, index);
    }

    /**
     * 获取一个字节中几个位组合而成的数据
     *
     * @param index         字节索引
     * @param startBitIndex 起始位索引
     * @param bitLength     位长度
     * @return 整形值
     */
    public int getByteToInt(int index, int startBitIndex, int bitLength) {
        this.checkCondition(index);
        return ByteUtil.toUInt8(this.data[index], startBitIndex, bitLength);
    }

    /**
     * 获取int16数据
     *
     * @param index 索引
     * @return int16数据
     */
    public short getInt16(int index) {
        this.checkCondition(index);
        return ShortUtil.toInt16(this.data, index, this.littleEndian);
    }

    /**
     * 获取uint16数据
     *
     * @param index 索引
     * @return uint16数据
     */
    public int getUInt16(int index) {
        this.checkCondition(index);
        return ShortUtil.toUInt16(this.data, index, this.littleEndian);
    }

    /**
     * 获取int32数据
     *
     * @param index 索引
     * @return int32数据
     */
    public int getInt32(int index) {
        this.checkCondition(index);
        return IntegerUtil.toInt32(this.reorderByFormatIn4Bytes(this.data, index), 0, this.littleEndian);
    }

    /**
     * 获取uint32数据
     *
     * @param index 索引
     * @return uint32数据
     */
    public long getUInt32(int index) {
        this.checkCondition(index);
        return IntegerUtil.toUInt32(this.reorderByFormatIn4Bytes(this.data, index), 0, this.littleEndian);
    }

    /**
     * 获取float32数据
     *
     * @param index 索引
     * @return float32数据
     */
    public float getFloat32(int index) {
        this.checkCondition(index);
        return FloatUtil.toFloat32(this.reorderByFormatIn4Bytes(this.data, index), 0, this.littleEndian);
    }

    /**
     * 获取float64数据
     *
     * @param index 索引
     * @return float64数据
     */
    public double getFloat64(int index) {
        this.checkCondition(index);
        return FloatUtil.toFloat64(this.reorderByFormatIn8Bytes(this.data, index), 0, this.littleEndian);
    }

    /**
     * 获取字符串
     *
     * @param index  索引
     * @param length 长度
     * @return 字符串
     */
    public String getString(int index, int length) {
        return this.getString(index, length, StandardCharsets.US_ASCII);
    }

    /**
     * 获取字符串
     *
     * @param index   索引
     * @param length  长度
     * @param charset 字符集
     * @return 字符串
     */
    public String getString(int index, int length, Charset charset) {
        this.checkCondition(index + length - 1);
        return ByteUtil.toStr(this.data, index, length, charset);
    }
}
