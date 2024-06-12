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


import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EMessageType;

/**
 * Header构建器
 *
 * @author xingshuang
 */
public class HeaderBuilder {

    private HeaderBuilder() {
        // NOOP
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return Header
     */
    public static Header fromBytes(final byte[] data) {
        EMessageType messageType = EMessageType.from(data[1]);

        switch (messageType) {
            case JOB:
                return Header.fromBytes(data);
            case ACK:
            case ACK_DATA:
                return AckHeader.fromBytes(data);
            case USER_DATA:
                return null;
            default:
                throw new S7CommException("Header message type not recognized");
        }
    }
}
