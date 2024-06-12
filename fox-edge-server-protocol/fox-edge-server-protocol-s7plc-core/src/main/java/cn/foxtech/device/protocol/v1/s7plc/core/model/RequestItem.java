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


import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EArea;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EParamVariableType;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.ESyntaxID;
import cn.foxtech.device.protocol.v1.s7plc.core.utils.IntegerUtil;
import lombok.Data;

/**
 * 标准数据读取请求项
 *
 * @author xingshuang
 */
@Data
public class RequestItem extends RequestBaseItem {

    public static final int BYTE_LENGTH = 12;

    public RequestItem() {
        this.specificationType = (byte) 0x12;
        this.lengthOfFollowing = 0x0A;
        this.syntaxId = ESyntaxID.S7ANY;
    }

    /**
     * 变量的类型和长度BIT，BYTE，WORD，DWORD，COUNTER <br>
     * 字节大小：1 <br>
     * 字节序数：3
     */
    private EParamVariableType variableType = EParamVariableType.BYTE;

    /**
     * 读取长度 <br>
     * 字节大小：2 <br>
     * 字节序数：4-5
     */
    private int count = 0x0000;

    /**
     * 即 DB 编号，如果访问的不是DB区域，此处为0x0000 <br>
     * 字节大小：2 <br>
     * 字节序数：6-7
     */
    private int dbNumber = 0x0000;

    /**
     * 存储区类型DB存储区 <br>
     * 字节大小：1 <br>
     * 字节序数：8
     */
    private EArea area = EArea.INPUTS;

    /**
     * 字节地址，位于开始字节地址address中3个字节，从第4位开始计数 <br>
     * 字节大小：3 <br>
     * 字节序数：9-11
     */
    private int byteAddress = 0;

    /**
     * 位地址，位于开始字节地址address中3个字节的最后3位
     */
    private int bitAddress = 0;

    @Override
    public int byteArrayLength() {
        return BYTE_LENGTH;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(BYTE_LENGTH)
                .putByte(this.specificationType)
                .putByte(this.lengthOfFollowing)
                .putByte(this.syntaxId.getCode())
                .putByte(this.variableType.getCode())
                .putShort(this.count)
                .putShort(this.dbNumber)
                .putByte(this.area.getCode())
                // 只有3个字节，因此只取后面的3字节，第一个字节舍弃
                .putBytes(IntegerUtil.toByteArray((this.byteAddress << 3) + this.bitAddress), 1)
                .getData();
    }

    /**
     * 复制一个新对象
     *
     * @return requestItem
     */
    public RequestItem copy() {
        RequestItem requestItem = new RequestItem();
        requestItem.specificationType = this.specificationType;
        requestItem.lengthOfFollowing = this.lengthOfFollowing;
        requestItem.syntaxId = this.syntaxId;
        requestItem.variableType = this.variableType;
        requestItem.count = this.count;
        requestItem.dbNumber = this.dbNumber;
        requestItem.area = this.area;
        requestItem.byteAddress = this.byteAddress;
        requestItem.bitAddress = this.bitAddress;
        return requestItem;
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return RequestItem
     */
    public static RequestItem fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * 字节数组数据解析
     *
     * @param data   字节数组数据
     * @param offset 偏移量
     * @return RequestItem
     */
    public static RequestItem fromBytes(final byte[] data, final int offset) {
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        RequestItem requestItem = new RequestItem();
        requestItem.specificationType = buff.getByte();
        requestItem.lengthOfFollowing = buff.getByteToInt();
        requestItem.syntaxId = ESyntaxID.from(buff.getByte());
        requestItem.variableType = EParamVariableType.from(buff.getByte());
        requestItem.count = buff.getUInt16();
        requestItem.dbNumber = buff.getUInt16();
        requestItem.area = EArea.from(buff.getByte());
        requestItem.byteAddress = IntegerUtil.toInt32In3Bytes(data, 9 + offset) >> 3;
        requestItem.bitAddress = buff.getByte(11 + offset) & 0x07;
        return requestItem;
    }

    /**
     * 通过参数创建请求项
     *
     * @param variableType 参数类型
     * @param count        数据个数
     * @param area         区域
     * @param dbNumber     DB块编号
     * @param byteAddress  字节地址
     * @param bitAddress   位地址
     * @return 请求项
     */
    public static RequestItem createByParams(EParamVariableType variableType, int count, EArea area, int dbNumber, int byteAddress, int bitAddress) {
        RequestItem requestItem = new RequestItem();
        requestItem.setVariableType(variableType);
        requestItem.setCount(count);
        requestItem.setArea(area);
        requestItem.setDbNumber(dbNumber);
        requestItem.setByteAddress(byteAddress);
        requestItem.setBitAddress(bitAddress);
        return requestItem;
    }
}
