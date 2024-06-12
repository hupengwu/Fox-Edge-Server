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
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 停止参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlcStopParameter extends Parameter implements IObjectByteArray {

    /**
     * 未知字节，固定参数 <br>
     * 字节大小：5 <br>
     * 字节序数：1-5
     */
    private byte[] unknownBytes = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 服务名长度，后续字节长度，不包含自身 <br>
     * 字节大小：1 <br>
     * 字节序数：6
     */
    private int lengthPart = 0;

    /**
     * 程序调用的服务名
     */
    private String piService = "";

    public void setPiService(String piService) {
        this.piService = piService;
        this.lengthPart = this.piService.length();
    }

    public PlcStopParameter() {
        this.functionCode = EFunctionCode.PLC_STOP;
    }

    @Override
    public int byteArrayLength() {
        return 7 + this.lengthPart;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(7 + this.lengthPart)
                .putByte(this.functionCode.getCode())
                .putBytes(this.unknownBytes)
                .putByte(this.lengthPart)
                .putString(this.piService)
                .getData();
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return PlcStopParameter
     */
    public static PlcStopParameter fromBytes(final byte[] data) {
        if (data.length < 7) {
            // StopParameter解析有误，StopParameter字节数组长度 < 7
            throw new S7CommException("The StopParameter resolution is incorrect. The StopParameter byte array length is less than 7");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        PlcStopParameter parameter = new PlcStopParameter();
        parameter.functionCode = EFunctionCode.from(buff.getByte());
        parameter.unknownBytes = buff.getBytes(5);
        parameter.lengthPart = buff.getByteToInt();
        parameter.piService = parameter.lengthPart == 0 ? "" : buff.getString(parameter.lengthPart);
        return parameter;
    }

    /**
     * 创建默认对象
     *
     * @return PlcStopParameter
     */
    public static PlcStopParameter createDefault() {
        PlcStopParameter parameter = new PlcStopParameter();
        parameter.setPiService("P_PROGRAM");
        return parameter;
    }
}
