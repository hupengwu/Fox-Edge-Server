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
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.ESyntaxID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 读写参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReadWriteParameter extends Parameter implements IObjectByteArray {

    /**
     * Request Item结构的数量 <br>
     * 字节大小：1 <br>
     * 字节序数：1
     */
    private int itemCount = 0x00;

    /**
     * 可重复的请求项
     */
    private List<RequestBaseItem> requestItems = new ArrayList<>();

    /**
     * 添加请求项
     *
     * @param item 项
     */
    public void addItem(RequestBaseItem item) {
        this.requestItems.add(item);
        this.itemCount = this.requestItems.size();
    }

    /**
     * 添加请求项列表
     *
     * @param items 请求项列表
     */
    public void addItem(Collection<? extends RequestBaseItem> items) {
        this.requestItems.addAll(items);
        this.itemCount = this.requestItems.size();
    }

    @Override
    public int byteArrayLength() {
        return 2 + this.requestItems.stream().mapToInt(RequestBaseItem::byteArrayLength).sum();
    }

    @Override
    public byte[] toByteArray() {
        int length = 2 + this.requestItems.stream().mapToInt(RequestBaseItem::byteArrayLength).sum();
        ByteWriteBuff buff = ByteWriteBuff.newInstance(length)
                .putByte(this.functionCode.getCode())
                .putByte(this.itemCount);
        for (RequestBaseItem requestItem : this.requestItems) {
            buff.putBytes(requestItem.toByteArray());
        }
        return buff.getData();
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return ReadWriteParameter
     */
    public static ReadWriteParameter fromBytes(final byte[] data) {
        if (data.length < 2) {
            // Parameter解析有误，parameter字节数组长度 < 2
            throw new S7CommException("Parameter parsing error, parameter byte array length < 2");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        ReadWriteParameter readWriteParameter = new ReadWriteParameter();
        readWriteParameter.functionCode = EFunctionCode.from(buff.getByte());
        readWriteParameter.itemCount = buff.getByteToInt();
        if (readWriteParameter.itemCount == 0) {
            return readWriteParameter;
        }
        // 读写返回时，只有功能码和个数
        if (data.length == 2) {
            return readWriteParameter;
        }
        int off = 2;
        for (int i = 0; i < readWriteParameter.itemCount; i++) {
            RequestBaseItem item = parserItem(data, off);
            readWriteParameter.requestItems.add(item);
            off += item.byteArrayLength();
        }
        return readWriteParameter;
    }

    /**
     * 解析字节数组数据
     *
     * @param data   字节数组数据
     * @param offset 偏移量
     * @return RequestBaseItem
     */
    public static RequestBaseItem parserItem(final byte[] data, final int offset) {
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        byte aByte = buff.getByte(2 + offset);
        ESyntaxID syntaxID = ESyntaxID.from(aByte);
        switch (syntaxID) {
            case S7ANY:
                return RequestItem.fromBytes(data, offset);
            case NCK:
                return RequestNckItem.fromBytes(data, offset);
            default:
                // 无法解析RequestBaseItem对应的类型
                throw new S7CommException("Unable to resolve the corresponding type of RequestBaseItem");
        }
    }

    /**
     * 创建默认的请求参数
     *
     * @param functionCode 功能码
     * @param requestItems 请求项
     * @return ReadWriteParameter
     */
    public static ReadWriteParameter createReqParameter(EFunctionCode functionCode, Collection<? extends RequestBaseItem> requestItems) {
        ReadWriteParameter parameter = new ReadWriteParameter();
        parameter.functionCode = functionCode;
        parameter.addItem(requestItems);
        return parameter;
    }

    /**
     * 创建响应参数
     *
     * @param request 对应请求对象
     * @return 读写参数
     */
    public static ReadWriteParameter createAckParameter(ReadWriteParameter request) {
        ReadWriteParameter parameter = new ReadWriteParameter();
        parameter.functionCode = request.functionCode;
        parameter.itemCount = request.itemCount;
        return parameter;
    }
}
