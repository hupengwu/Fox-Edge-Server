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

package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EPduType;
import cn.foxtech.device.protocol.v1.s7plc.core.utils.BooleanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * COTP数据部分Describes a COTP TPDU (Transport protocol data unit)
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class COTPData extends COTP implements IObjectByteArray {

    public static final int BYTE_LENGTH = 3;
    /**
     * TPDU编号 <br>
     * 字节大小：1，后面7位 <br>
     * 字节序数：2
     */
    private int tpduNumber = 0x00;

    /**
     * 是否最后一个数据单元 <br>
     * 字节大小：1，最高位，7位 <br>
     * 字节序数：2
     */
    private boolean lastDataUnit = true;

    @Override
    public int byteArrayLength() {
        return BYTE_LENGTH;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(BYTE_LENGTH)
                .putByte(this.length)
                .putByte(this.pduType.getCode())
                // TPDU编号和是否最后一个数据单元组合成一个字节，最高位表示是否最后一个
                .putByte((byte) (BooleanUtil.setBit((byte) 0x00, 7, this.lastDataUnit) | (this.tpduNumber & 0xFF)))
                .getData();
    }

    /**
     * 通过字节数组转换为COTPData对象
     *
     * @param data 字节数组
     * @return COTPData对象
     */
    public static COTPData fromBytes(final byte[] data) {
        if (data.length < BYTE_LENGTH) {
            // COTPData数据字节长度不够，无法解析
            throw new S7CommException("COTPData Data byte length is not enough to parse");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        COTPData cotpData = new COTPData();
        cotpData.length = buff.getByteToInt();
        cotpData.pduType = EPduType.from(buff.getByte());
        cotpData.tpduNumber = buff.getByte() & 0x7F;
        cotpData.lastDataUnit = buff.getBoolean(2,7);
        return cotpData;
    }

    /**
     * DtData COTP 数据部分
     *
     * @return COTPData对象
     */
    public static COTPData createDefault() {
        COTPData connection = new COTPData();
        connection.length = 2;
        connection.pduType = EPduType.DT_DATA;
        connection.tpduNumber = 0;
        connection.lastDataUnit = true;
        return connection;
    }
}
