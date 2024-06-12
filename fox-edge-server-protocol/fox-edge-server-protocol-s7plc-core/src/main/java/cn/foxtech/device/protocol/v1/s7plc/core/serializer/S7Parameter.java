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

package cn.foxtech.device.protocol.v1.s7plc.core.serializer;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EDataType;
import lombok.Data;

/**
 * S7参数
 *
 * @author xingshuang
 */
@Data
public class S7Parameter {

    /**
     * 地址
     */
    protected String address = "";

    /**
     * 数据类型
     */
    protected EDataType dataType = EDataType.BYTE;

    /**
     * 个数
     * 除字节Byte和String类型外，其他类型对应的count必须为1
     */
    protected Integer count = 1;

    /**
     * 对应的值
     */
    protected Object value;

    public S7Parameter() {
    }

    public S7Parameter(String address, EDataType dataType) {
        this.address = address;
        this.dataType = dataType;
    }

    public S7Parameter(String address, EDataType dataType, Integer count) {
        this.address = address;
        this.dataType = dataType;
        this.count = count;
    }

    public S7Parameter(String address, EDataType dataType, Integer count, Object value) {
        this.address = address;
        this.dataType = dataType;
        this.count = count;
        this.value = value;
    }
}
